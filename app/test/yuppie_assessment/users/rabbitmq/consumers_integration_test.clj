(ns yuppie-assessment.users.rabbitmq.consumers-integration-test
  (:require [clojure.test :refer :all]
            [clojure.java.jdbc :as jdbc]
            [yuppie-assessment.users.repository.mysql :as mysql-repo]
            [yuppie-assessment.users.rabbitmq.queues :as q]
            [yuppie-assessment.mysql.client :refer [user-db mysql-conn]]
            [yuppie-assessment.rabbitmq.client :as rabbitmq]
            [mount.core :as mount]))

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

(deftest ^:integration test-upload-profile-picture
  (testing "can upload profile picture"
    (let [id "my-cloud-test-id"
          test-url "https://cloudinary-devs.github.io/cld-docs-assets/assets/images/coffee_cup.jpg"
          profile {:id id
                   :first-name "my-first-name"
                   :last-name "my-last-name"
                   :email-address "my@there.com"
                   :profile-picture-url test-url}
          _ (mysql-repo/insert-user-profile user-db profile)
          _ (rabbitmq/publish-map q/user-profile-created-queue profile)
          _ (Thread/sleep 5000)
          updated-profile (mysql-repo/get-user-profile-by-id user-db id)]      
      (is (string? (:profile-picture-url updated-profile)))
      (is (not= test-url (:profile-picture-url updated-profile))))))
