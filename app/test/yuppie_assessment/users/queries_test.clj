(ns yuppie-assessment.users.queries-test
  (:require [clojure.test :refer :all]
            [yuppie-assessment.assert-helpers :refer [test-mock]]
            [yuppie-assessment.users.queries :as user-queries]
            [yuppie-assessment.users.repository.mysql :as mysql-repo]))

(deftest test-get-user-profile-by-email
  (testing "can get user by email"
    (let [email "me@here.com"
          profile {:id "my-id"
                   :first-name "my-first-name"
                   :last-name "my-last-name"
                   :email-address email
                   :profile-picture-url "https://my.com/pic"}]
      (with-redefs [mysql-repo/get-user-profile-by-email
                    (test-mock "get-user-profile-by-email" [keyword? #(= email %)] profile)]
        (= profile
           (user-queries/get-user-profile-by-email email))))))
