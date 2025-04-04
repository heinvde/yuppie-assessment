(ns yuppie-assessment.users.repository.mysql-test
  (:require [clojure.test :refer :all]
            [yuppie-assessment.users.repository.mysql :as mysql-repo]
            [clojure.java.jdbc :as jdbc]
            [mount.core :as mount]
            [yuppie-assessment.config]
            [yuppie-assessment.mysql.client :refer [user-db mysql-conn]]))

(defn- clean-database
  "Clean up the test database"
  []
  (jdbc/execute! (mysql-conn user-db)
                 ["DELETE FROM user_profiles"]))

(use-fixtures :once
  (fn [run-tests]
    (mount/start #'yuppie-assessment.config/config
                 #'yuppie-assessment.mysql.client/mysql-conn)
    (clean-database)
    (run-tests)
    (clean-database)
    (mount/stop)))

(use-fixtures :each
  (fn [run-tests]
    (clean-database)
    (run-tests)
    (clean-database)))

(deftest ^:integration test-insert-user-profile
  (testing "can insert new profile into MySQL db"
    (let [id (random-uuid)
          profile {:id id
                   :first-name "my-first-name"
                   :last-name "my-last-name"
                   :email-address "my-email"}]
      (mysql-repo/insert-user-profile user-db profile)
      (is (= {:id (str id)
              :first_name "my-first-name"
              :last_name "my-last-name"
              :email "my-email"}
             (jdbc/query (mysql-conn user-db)
                         ["SELECT * FROM user_profiles WHERE id = ?" (str id)]
                         {:result-set-fn first}))))))

(deftest ^:integration test-update-user-profile-by-email
  (testing "can insert new profile into MySQL db"
    (let [id (random-uuid)
          profile {:id id
                   :first-name "my-first-name"
                   :last-name "my-last-name"
                   :email-address "my-email"}
          updated-profile {:id "shouldbeignore"
                           :first-name "my-first-name-2"
                           :last-name "my-last-name-2"
                           :email-address "my-email"}]
      (mysql-repo/insert-user-profile user-db profile)
      (mysql-repo/update-user-profile-by-email user-db (:email-address profile) updated-profile)
      (is (= {:id (str id)
              :first_name "my-first-name-2"
              :last_name "my-last-name-2"
              :email "my-email"}
             (jdbc/query (mysql-conn user-db)
                         ["SELECT * FROM user_profiles WHERE id = ?" (str id)]
                         {:result-set-fn first}))))))

(deftest ^:integration test-get-user-profile-by-email
  (testing "can insert new profile into MySQL db"
    (let [id (random-uuid)
          profile {:id id
                   :first-name "my-first-name"
                   :last-name "my-last-name"
                   :email-address "my-email"}]
      (mysql-repo/insert-user-profile user-db profile)
      (is (= {:id id
              :first-name "my-first-name"
              :last-name "my-last-name"
              :email-address "my-email"}
             (mysql-repo/get-user-profile-by-email user-db (:email-address profile)))))))
