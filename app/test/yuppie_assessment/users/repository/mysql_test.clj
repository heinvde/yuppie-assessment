(ns yuppie-assessment.users.repository.mysql-test
  (:require [clojure.test :refer :all]
            [yuppie-assessment.users.repository.mysql :as mysql-repo]
            [clojure.java.jdbc :as jdbc]
            [mount.core :as mount]
            [yuppie-assessment.config]
            [yuppie-assessment.mysql.client :refer [user-db mysql-conn]]
            [yuppie-assessment.users.errors :as errors]))

(defn- clean-database
  "Clean up the test database"
  []
  (jdbc/execute! (mysql-conn user-db)
                 ["DELETE FROM user_profiles"]))

(use-fixtures :once
  (fn [run-tests]
    (mount/start #'yuppie-assessment.config/config
                 #'yuppie-assessment.mysql.client/mysql-conn)
    (run-tests)
    (clean-database)
    (mount/stop)))

(use-fixtures :each
  (fn [run-tests]
    (clean-database)
    (run-tests)))

(deftest ^:integration test-insert-user-profile
  (testing "can insert new profile into MySQL db"
    (let [id (random-uuid)
          date (str (java.util.Date.))
          profile {:id id
                   :first-name "my-first-name"
                   :last-name "my-last-name"
                   :email-address "my-email"
                   :profile-picture-url "https://my.com/pic"}]
      (mysql-repo/insert-user-profile user-db profile)
      (is (= {:id (str id)
              :first_name "my-first-name"
              :last_name "my-last-name"
              :email "my-email"
              :profile_picture_url "https://my.com/pic"
              :date_created date
              :date_updated date}
             (jdbc/query (mysql-conn user-db)
                         ["SELECT * FROM user_profiles WHERE id = ?" (str id)]
                         {:result-set-fn first})))))
  (testing "throws error on duplicate email address"
    (let [id (random-uuid)
          profile {:id id
                   :first-name "my-first-name"
                   :last-name "my-last-name"
                   :email-address "second@there.com"
                   :profile-picture-url "https://my.com/pic"}]
      (mysql-repo/insert-user-profile user-db profile)
      (try
        (mysql-repo/insert-user-profile user-db profile)
        (is false "Should have thrown an error")
        (catch Exception ex (is (= errors/message-already-exists (.getMessage ex))
                                (= errors/type-already-exists (-> ex ex-data :type))))))))

(deftest ^:integration test-update-user-profile-by-email
  (testing "can do update on existing profile in MySQL db"
    (let [id (random-uuid)
          email "first@here.com"
          date (str (java.util.Date.))
          profile {:id id
                   :first-name "my-first-name"
                   :last-name "my-last-name"
                   :email-address email
                   :profile-picture-url "https://my.com/pic"}
          updated-profile {:id "shouldbeignore"
                           :first-name "my-first-name-2"
                           :last-name "my-last-name-2"
                           :email-address email}]
      (mysql-repo/insert-user-profile user-db profile)
      (mysql-repo/update-user-profile-by-email user-db (:email-address profile) updated-profile)
      (is (= {:id (str id)
              :first_name "my-first-name-2"
              :last_name "my-last-name-2"
              :email email
              :profile_picture_url "https://my.com/pic"
              :date_created date
              :date_updated date}
             (jdbc/query (mysql-conn user-db)
                         ["SELECT * FROM user_profiles WHERE id = ?" (str id)]
                         {:result-set-fn first})))))
  (testing "can do minimal update on existing profile in MySQL db"
    (let [id (random-uuid)
          email "second@here.com"
          date (str (java.util.Date.))
          profile {:id id
                   :first-name "my-first-name"
                   :last-name "my-last-name"
                   :email-address email
                   :profile-picture-url "https://my.com/pic"}
          updated-profile {:first-name "my-first-name-2"}]
      (mysql-repo/insert-user-profile user-db profile)
      (mysql-repo/update-user-profile-by-email user-db (:email-address profile) updated-profile)
      (is (= {:id (str id)
              :first_name "my-first-name-2"
              :last_name "my-last-name"
              :email email
              :profile_picture_url "https://my.com/pic"
              :date_created date
              :date_updated date}
             (jdbc/query (mysql-conn user-db)
                         ["SELECT * FROM user_profiles WHERE id = ?" (str id)]
                         {:result-set-fn first}))))))

(deftest ^:integration test-get-user-profile-by-email
  (testing "can insert new profile into MySQL db"
    (let [id (random-uuid)
          date (str (java.util.Date.))
          profile {:id id
                   :first-name "my-first-name"
                   :last-name "my-last-name"
                   :email-address "my-email"
                   :profile-picture-url "https://my.com/pic"}]
      (mysql-repo/insert-user-profile user-db profile)
      (is (= {:id (str id)
              :first-name "my-first-name"
              :last-name "my-last-name"
              :email-address "my-email"
              :profile-picture-url "https://my.com/pic"
              :date-created date
              :date-updated date}
             (mysql-repo/get-user-profile-by-email user-db (:email-address profile)))))))
