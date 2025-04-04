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

(deftest ^:integration test-insert-user-profile
  (testing "can insert new profile into MySQL db"
    (let [profile {:id "my-id"
                   :first-name "my-first-name"
                   :last-name "my-last-name"
                   :email-address "my-email"}]
      (mysql-repo/insert-user-profile user-db profile)
      (is (= {:id "my-id"
              :first_name "my-first-name"
              :last_name "my-last-name"
              :email "my-email"}
             (jdbc/query (mysql-conn user-db)
                         ["SELECT * FROM user_profiles WHERE id = ?" (:id profile)]
                         {:result-set-fn first}))))))
