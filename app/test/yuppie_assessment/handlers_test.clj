(ns yuppie-assessment.handlers-test
  (:require [clojure.test :refer :all]
            [mount.core :as mount]
            [clojure.data.json :as json]
            [yuppie-assessment.config :refer [config]]
            [yuppie-assessment.handlers :as handlers]
            [yuppie-assessment.users.updates :as user-updates]
            [yuppie-assessment.users.queries :as user-queries]
            [yuppie-assessment.users.errors :as user-errors]))

(deftest test-handle-check
  (testing "returns valid health check response"
    (is (= {:status 200
            :headers {"Content-Type" "application/json"}
            :body "{\"message\":\"OK\"}"}
           (handlers/handle-health-check)))))

(use-fixtures :once
  (fn [run-tests]
    (mount/start #'yuppie-assessment.config/config)
    (run-tests)
    (mount/stop)))

(deftest handle-oauth2-redirect
  (testing "returns redirect response with oauth2 url"
    (let [result (handlers/handle-oauth2-redirect)]
      (is (= 302 (:status result)))
      (is (string? (-> result :headers (get "Location")))))))

(def state-key (fn [] (-> config :google :oauth2 :state-key)))

(deftest test-handle-oauth2-callback
  (testing "can handle oauth2 callback to create new profile"
    (with-redefs [user-updates/create-profile-with-google-oauth
                  (fn [code]
                    (is (= "my-code" code))
                    {:id "my-id"
                     :first-name "John"
                     :last-name "Doe"
                     :email-address "me@there.com"})]
      (let [request {:query-params {"code" "my-code" "state" (state-key)}}
            result (handlers/handle-oauth2-callback request)]
        (is (= 200 (:status result)))
        (is (string? (-> result :body))))))
  (testing "can handle oauth2 callback to profile already exists"
    (with-redefs [user-updates/create-profile-with-google-oauth
                  (fn [_] (throw (ex-info user-errors/message-already-exists
                                          {:type user-errors/type-already-exists
                                           :profile {:email-address "here@there.com"}})))
                  user-queries/get-profile-by-email
                  (fn [email]
                    (is (= "here@there.com" email))
                    {:first-name "John"
                     :last-name "Doe"})]
      (let [request {:query-params {"code" "my-code" "state" (state-key)}}
            result (handlers/handle-oauth2-callback request)]
        (is (= 200 (:status result)))
        (is (string? (-> result :body))))))
  (testing "can handle oauth2 callback invalid state key"
    (let [request {:query-params {"code" "my-code" "state" "invalidstatekeyx"}}
          result (handlers/handle-oauth2-callback request)]
      (is (= 401 (:status result)))
      (is (= "Unauthorized"
             (-> result :body))))))

(deftest test-handle-get-user-profile
  (testing "can handle get user profile by id"
    (with-redefs [user-queries/get-profile-by-id
                  (fn [id]
                    (is (= "my-id" id))
                    {:first-name "John"
                     :last-name "Doe"})]
      (let [request {:params {:id "my-id"}}
            result (handlers/handle-get-user-profile request)]
        (is (= 200 (:status result)))
        (is (= (json/write-str {:first-name "John" :last-name "Doe"})
               (-> result :body))))))
  (testing "can handle get user profile by id not found"
    (with-redefs [user-queries/get-profile-by-id
                  (fn [_] nil)]
      (let [request {:params {:id "my-id"}}
            result (handlers/handle-get-user-profile request)]
        (is (= 404 (:status result)))
        (is (= (json/write-str {:message "User not found"})
               (-> result :body)))))))
