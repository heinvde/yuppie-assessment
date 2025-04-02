(ns yuppie-assessment.app-test
  (:require [clojure.test :refer :all]
            [yuppie-assessment.app :refer :all]
            [ring.mock.request :as mock]
            [clojure.data.json :as json]))

(deftest test-app-route-get-check
  (testing "GET /check returns valid response"
    (let [{:keys [body status]} (app (mock/request :get "/check"))]
      (is (= body (json/write-str {:message "OK"})))
      (is (= status 200)))))

(deftest test-app-route-not-found
  (testing "GET /invalidx returns not found"
    (let [{:keys [body status]} (app (mock/request :get "/invalidx"))]
      (is (= "Not Found" body))
      (is (= 404 status)))))
