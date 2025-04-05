(ns yuppie-assessment.users.model.profile-test
  (:require [clojure.test :refer :all]
            [yuppie-assessment.users.model.profile :as model]))

(deftest test-profile->mysql-profile
  (testing "can format db profile model"
    (is (= {:id "my-id"
            :first_name "my-first-name"
            :last_name "my-last-name"
            :email "my-email"
            :profile_picture_url "https://my.com/pic"
            :date_created "2023-10-01T12:00:00Z"
            :date_updated "2023-10-01T12:00:00Z"}
           (model/profile->mysql-profile 
            {:id "my-id"
             :first-name "my-first-name"
             :last-name "my-last-name"
             :email-address "my-email"
             :profile-picture-url "https://my.com/pic"
             :date-created "2023-10-01T12:00:00Z"
             :date-updated "2023-10-01T12:00:00Z"}))))
  (testing "can format minimum db profile model"
    (is (= {:id "my-id"
            :first_name "my-first-name"
            :last_name ""
            :email "my-email"
            :profile_picture_url nil
            :date_created "2023-10-01T12:00:00Z"
            :date_updated "2023-10-01T12:00:00Z"}
           (model/profile->mysql-profile
            {:id "my-id"
             :first-name "my-first-name"
             :email-address "my-email"
             :date-updated "2023-10-01T12:00:00Z"
             :date-created "2023-10-01T12:00:00Z"})))))

(deftest test-profile->mysql-update
  (testing "can format db profile update model"
    (is (= {:first_name "my-first-name"
            :last_name "my-last-name"
            :email "my-email"
            :profile_picture_url "https://my.com/pic"
            :date_updated "2023-10-01T12:00:00Z"}
           (model/profile->mysql-update 
            {:first-name "my-first-name"
             :last-name "my-last-name"
             :email-address "my-email"
             :profile-picture-url "https://my.com/pic"
             :date-created "2023-10-01T12:00:00Z"
             :date-updated "2023-10-01T12:00:00Z"}))))
  (testing "can format minimum db profile update model"
    (is (= {:first_name "my-first-name"}
           (model/profile->mysql-update
            {:first-name "my-first-name"})))))


(deftest test-mysql-profile->profile
  (testing "can format db profile model"
    (is (= {:id "my-id"
            :first-name "my-first-name"
            :last-name "my-last-name"
            :email-address "my-email"
            :profile-picture-url "https://my.com/pic"
            :date-created "2023-10-01T12:00:00Z"
            :date-updated "2023-10-01T12:00:00Z"}
           (model/mysql-profile->profile
            {:id "my-id"
             :first_name "my-first-name"
             :last_name "my-last-name"
             :email "my-email"
             :profile_picture_url "https://my.com/pic"
             :date_created "2023-10-01T12:00:00Z"
             :date_updated "2023-10-01T12:00:00Z"}))))
  (testing "can format minimum db profile model"
    (is (= {:id "my-id"
            :first-name "my-first-name"
            :email-address "my-email"
            :profile-picture-url nil
            :last-name nil
            :date-updated nil
            :date-created nil}
           (model/mysql-profile->profile
            {:id "my-id"
             :first_name "my-first-name"
             :email "my-email"})))))
