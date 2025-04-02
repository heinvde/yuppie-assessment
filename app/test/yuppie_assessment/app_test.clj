(ns yuppie-assessment.app-test
  (:require [clojure.test :refer :all]
            [yuppie-assessment.app :refer :all]
            [ring.mock.request :as mock]))

(deftest test-app-route-not-found
  (testing "GET /invalidx returns not found"
    (let [{:keys [body status]} (app (mock/request :get "/invalidx"))]
      (is (= "Not Found" body))
      (is (= 404 status)))))
