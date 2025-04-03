(ns yuppie-assessment.app-test
  (:require [clojure.test :refer :all]
            [yuppie-assessment.app :refer :all]
            [ring.mock.request :as mock]
            [clojure.data.json :as json]))


(deftest test-app-route-get-check
  (testing "GET /check returns valid response"
    (let [{:keys [body status]} (app (mock/request :get "/check"))]
      (is (= (json/write-str {:message "OK"}) body))
      (is (= 200 status)))))

(deftest test-app-route-get-auth-verify
  (testing "GET /auth/verify redirects to Google OAuth2"
    (let [{:keys [headers status]} (app (mock/request :get "/auth/verify"))]
      (is (contains? headers "Location"))
      (is (= 302 status)))))

(deftest test-app-route-get-index
  (testing "GET / has successfull response"
    (let [{:keys [status]} (app (mock/request :get "/"))]
      (is (and (>= status 200)
               (< status 500))))))

(deftest test-app-route-not-found
  (testing "GET /invalidx returns not found"
    (let [{:keys [body status]} (app (mock/request :get "/invalidx"))]
      (is (= "Not Found" body))
      (is (= 404 status)))))
