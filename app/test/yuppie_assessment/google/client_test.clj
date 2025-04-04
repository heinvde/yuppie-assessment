(ns yuppie-assessment.google.client-test
  (:require [clojure.test :refer :all]
            [yuppie-assessment.google.client :as google]
            [clojure.data.json :as json]
            [clj-http.client :as http]))

(deftest test-get-oath2-request-url
  (testing "can get formatted url with all parameters"
    (is (= "https://accounts.google.com/o/oauth2/v2/auth?client_id=123&redirect_uri=http%3A%2F%2Fwww.example.com%2Fauth&response_type=code&scope=https%3A%2F%2Femail.google%20https%3A%2F%2Fprofile.google&access_type=offline&include_granted_scopes=true&state=my-secret-state"
           (google/get-oath2-request-url {:client-id "123"
                                          :redirect-uri "http://www.example.com/auth"
                                          :scopes ["https://email.google" "https://profile.google"]
                                          :state "my-secret-state"}))))
  (testing "can get formatted url with some parameters"
    (is (= "https://accounts.google.com/o/oauth2/v2/auth?client_id=123&redirect_uri=http%3A%2F%2Fwww.example.com%2Fauth&response_type=code&scope=email&access_type=offline&include_granted_scopes=true&state=none"
           (google/get-oath2-request-url {:client-id "123"
                                          :redirect-uri "http://www.example.com/auth"
                                          :scopes ["email"]})))))

(deftest test-oauth2-code->access-token
  (testing "can get access token for oauth code"
    (let [expected-url "https://oauth2.googleapis.com/token"
          expected-options {:form-params {:code "test-code"
                                          :client_id "test-client-id"
                                          :client_secret "test-client-secret"
                                          :redirect_uri "http://test.com/yes"
                                          :grant_type "authorization_code"}}
          expected-result {:access-token "fake-access-token"
                           :expires-in 3600
                           :refresh-token "fake-refresh-token"}]
      (with-redefs [http/post
                    (fn [url options]
                      (is (= expected-url url))
                      (is (= expected-options options))
                      {:body (json/write-str {"access_token" "fake-access-token"
                                              "expires_in" 3600
                                              "refresh_token" "fake-refresh-token"})})]
        (is (= expected-result
               (google/oauth2-code->access-token {:code "test-code"
                                                  :client-id "test-client-id"
                                                  :client-secret "test-client-secret"
                                                  :redirect-uri "http://test.com/yes"}))))))
  (testing "can get access token for oauth code using multi arity version"
    (let [expected-url "https://oauth2.googleapis.com/token"
          expected-options {:form-params {:code "test-code"
                                          :client_id "test-client-id"
                                          :client_secret "test-client-secret"
                                          :redirect_uri "http://test.com/yes"
                                          :grant_type "authorization_code"}}
          expected-result {:access-token "fake-access-token"
                           :expires-in 3600
                           :refresh-token "fake-refresh-token"}]
      (with-redefs [http/post
                    (fn [url options]
                      (is (= expected-url url))
                      (is (= expected-options options))
                      {:body (json/write-str {"access_token" "fake-access-token"
                                              "expires_in" 3600
                                              "refresh_token" "fake-refresh-token"})})]
        (is (= expected-result
               (google/oauth2-code->access-token
                {:client-id "test-client-id" :client-secret "test-client-secret"}
                "test-code"
                "http://test.com/yes")))))))

(deftest test-get-user-profile
  (testing "can get user profile from google"
    (let [expected-url "https://people.googleapis.com/v1/people/me?personFields=names%2Cphotos%2CemailAddresses"
          expected-result {:first-name "My name"
                           :last-name "My surname"
                           :email-address "me@there.com"
                           :profile-picture-url "https://somewhere.com/photo.jpg"}
          expected-options {:headers {"Authorization" "Bearer my-fake-token"}}
          body (json/write-str {"names" [{"metadata" {"primary" true} "familyName" "My surname" "givenName" "My name"}]
                                "photos" [{"metadata" {"primary" true} "url" "https://somewhere.com/photo.jpg"}]
                                "emailAddresses" [{"metadata" {"primary" true} "value" "me@there.com"}]})]
      (with-redefs [http/get (fn [url options]
                               (is (= expected-url url))
                               (is (= expected-options options))
                               {:body body})]
        (is (= expected-result
               (google/get-user-profile {:access-token "my-fake-token"})))))))

(deftest test-map->query-string
  (testing "can convert map to query string"
    (is (= "key1=value1&key2=value2"
           (#'google/map->query-string {:key1 "value1" :key2 "value2"}))))
  (testing "can convert map to query string with URL encoding"
    (is (= "key1=value1&tryencode=1%202%203"
           (#'google/map->query-string {:key1 "value1" :tryencode "1 2 3"})))))

(deftest test-grab-profile-attribute
  (testing "can get primary attribute"
    (is (= "My name"
           (#'google/grab-profile-attribute
            {:names [{:metadata {:primary false} :name "My other name"}
                     {:metadata {:primary true} :name "My name"}]}
            :names
            :name))))
  (testing "can get no primary attribute"
    (is (= "My other name"
           (#'google/grab-profile-attribute
            {:names [{:metadata {:primary false} :name "My other name"}]}
            :names
            :name))))
  (testing "can get nil for no field"
     (is (= nil
            (#'google/grab-profile-attribute {} :names :name))))
  (testing "can get when no metadata"
    (is (= "My name"
         (#'google/grab-profile-attribute {:names [{:name "My name"}]} :names :name)))))

(deftest test-grab-user-profile
  (testing "can get primary attribute"
    (is (= {:first-name "My name"
            :last-name "My surname"
            :email-address "me@there.com"
            :profile-picture-url "https://someurl.com"}
           (#'google/grab-user-profile
            {:names [{:metadata {:primary true} :givenName "My name" :familyName "My surname"}]
             :emailAddresses [{:metadata {:primary false} :value "me@there.com"}]
             :photos [{:metadata {:primary true} :url "https://someurl.com"}]})))))

(deftest test-handle-http-exception
  (testing "throws bad request exception"
    (try
      (#'google/handle-http-exception (ex-info "Bad request" {:status 400 :body "Failed"}))
      (catch Exception ex
        (is (= "Bad request" (.getMessage ex)))
        (is (= :error-bad-request (-> ex ex-data :type)))
        (is (= 400 (-> ex ex-data :status)))
        (is (= "Failed" (-> ex ex-data :body))))))
  (testing "throws unauthorized request exception"
    (try
      (#'google/handle-http-exception (ex-info "Unauthorized" {:status 401 :body "Failed"}))
      (catch Exception ex
        (is (= "Unauthorized" (.getMessage ex)))
        (is (= :error-auth (-> ex ex-data :type)))
        (is (= 401 (-> ex ex-data :status)))
        (is (= "Failed" (-> ex ex-data :body))))))
  (testing "throws unknown request exception"
    (try
      (#'google/handle-http-exception (ex-info "Unkown error" {:status 488 :body "Failed"}))
      (catch Exception ex
        (is (= "Unkown error" (.getMessage ex)))
        (is (= :error-unknown (-> ex ex-data :type)))
        (is (= 488 (-> ex ex-data :status)))
        (is (= "Failed" (-> ex ex-data :body)))))))
