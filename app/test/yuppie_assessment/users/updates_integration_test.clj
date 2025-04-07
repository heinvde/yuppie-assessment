(ns yuppie-assessment.users.updates-integration-test
  (:require [clojure.test :refer :all]
            [mount.core :as mount]
            [clojure.java.jdbc :as jdbc]
            [yuppie-assessment.config]
            [yuppie-assessment.mysql.client :refer [mysql-conn user-db]]
            [yuppie-assessment.rabbitmq.client :as client]
            [yuppie-assessment.users.repository.mysql :as mysql-repo]
            [yuppie-assessment.users.queries :as user-queries]
            [yuppie-assessment.users.updates :as user-updates]))

(def check-profile
  (fn [expected profile]
    (and
     (or (uuid? (:id profile)) (string? (:id profile)))
     (= (:first-name expected) (:first-name profile))
     (= (:last-name expected) (:last-name profile))
     (= (:email-address expected) (:email-address profile)))))

(defn- clean-database
  "Clean up the test database"
  []
  (jdbc/execute! (mysql-conn user-db)
                 ["DELETE FROM user_profiles"]))

(use-fixtures :once
  (fn [run-tests]
    (mount/start)
    (run-tests)
    (clean-database)
    (mount/stop)))

(use-fixtures :each
  (fn [run-tests]
    (clean-database)
    (run-tests)))

(deftest test-create-user
  (testing "can create user"
    (let [profile {:first-name "my-first-name"
                   :last-name "my-last-name"
                   :email-address "my-email"
                   :profile-picture-url "https://cloudinary-devs.github.io/cld-docs-assets/assets/images/coffee_cup.jpg"}]
      (is (check-profile profile
                         (user-updates/create-profile profile))))))
