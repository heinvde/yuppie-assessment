(ns yuppie-assessment.handlers-test
  (:require [clojure.test :refer :all]
            [yuppie-assessment.handlers :as handlers]))

(deftest test-handle-check
  (testing "returns valid health check response"
    (is (= (handlers/handle-health-check)
           {:status 200
            :headers {"Content-Type" "application/json"}
            :body "{\"message\":\"OK\"}"}))))
