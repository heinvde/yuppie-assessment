(ns yuppie-assessment.handlers-test
  (:require [clojure.test :refer :all]
            [yuppie-assessment.handlers :as handlers]
            [yuppie-assessment.users.updates :as user-updates]))

(deftest test-handle-check
  (testing "returns valid health check response"
    (is (= {:status 200
            :headers {"Content-Type" "application/json"}
            :body "{\"message\":\"OK\"}"}
           (handlers/handle-health-check)))))

(deftest handle-oauth2-redirect
  (testing "returns redirect response with oauth2 url"
    (let [result (handlers/handle-oauth2-redirect)]
      (is (= 302 (:status result)))
      (is (string? (-> result :headers (get "Location")))))))

(deftest test-handle-oauth2-callback
  (testing "can handle oauth2 callback to create new profile"
    (with-redefs [user-updates/create-user-with-google-oauth
                  (fn [code]
                    (is (= "my-code" code))
                    {:id "my-id"
                     :first-name "John"
                     :last-name "Doe"
                     :email-address "me@there.com"})]
    (let [request {:query-params {"code" "my-code"}}
          result (handlers/handle-oauth2-callback request)]
        (is (= 200 (:status result)))
        (is (= "Welcome John Doe, your account has successfully been created."
               (-> result :body)))))))
