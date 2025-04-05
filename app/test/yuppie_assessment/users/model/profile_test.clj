(ns yuppie-assessment.users.model.profile-test
  (:require [clojure.test :refer :all]
            [yuppie-assessment.users.model.profile :as model]))

(deftest test-profile->mysql-profile
  (testing "can format db profile model"
    (is (= {:id "my-id"
            :first_name "my-first-name"
            :last_name "my-last-name"
            :email "my-email"
            :profile_picture_url "https://my.com/pic"}
           (model/profile->mysql-profile 
            {:id "my-id"
             :first-name "my-first-name"
             :last-name "my-last-name"
             :email-address "my-email"
             :profile-picture-url "https://my.com/pic"}))))
  (testing "can format minimum db profile model"
    (is (= {:id "my-id"
            :first_name "my-first-name"
            :last_name ""
            :email "my-email"
            :profile_picture_url nil}
           (model/profile->mysql-profile
            {:id "my-id"
             :first-name "my-first-name"
             :email-address "my-email"})))))

(deftest test-profile->mysql-update
  (testing "can format db profile update model"
    (is (= {:first_name "my-first-name"
            :last_name "my-last-name"
            :email "my-email"
            :profile_picture_url "https://my.com/pic"}
           (model/profile->mysql-update 
            {:first-name "my-first-name"
             :last-name "my-last-name"
             :email-address "my-email"
            :profile-picture-url "https://my.com/pic"}))))
  (testing "can format minimum db profile update model"
    (is (= {:first_name "my-first-name"}
           (model/profile->mysql-update
            {:first-name "my-first-name"})))))
