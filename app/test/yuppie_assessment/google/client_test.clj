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
                                                  :redirect-uri "http://test.com/yes"})))))))

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
