(ns yuppie-assessment.handlers-test
  (:require [clojure.test :refer :all]
            [yuppie-assessment.handlers :as handlers]))

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
      (is (string? (-> result :headers (get "Location")))))));
