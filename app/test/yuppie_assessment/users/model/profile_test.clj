(ns yuppie-assessment.users.model.profile-test
  (:require [clojure.test :refer :all]
            [yuppie-assessment.users.model.profile :as model]))

(deftest test-profile->mysql-profile
  (testing "can format db profile model"
    (is (= {:id "my-id"
            :first_name "my-first-name"
            :last_name "my-last-name"
            :email "my-email"}
           (model/profile->mysql-profile 
            {:id "my-id"
             :first-name "my-first-name"
             :last-name "my-last-name"
             :email-address "my-email"}))))
  (testing "can format minimum db profile model"
    (is (= {:id "my-id"
            :first_name "my-first-name"
            :last_name ""
            :email "my-email"}
           (model/profile->mysql-profile
            {:id "my-id"
             :first-name "my-first-name"
             :email-address "my-email"})))))
