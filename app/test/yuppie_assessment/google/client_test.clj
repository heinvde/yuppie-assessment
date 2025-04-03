(ns yuppie-assessment.google.client-test
  (:require [clojure.test :refer :all]
             [yuppie-assessment.google.client :as google]))

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