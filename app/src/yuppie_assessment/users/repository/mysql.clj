(ns yuppie-assessment.users.repository.mysql
  (:require [clojure.java.jdbc :as jdbc]
            [yuppie-assessment.users.model.profile :as model]
            [yuppie-assessment.mysql.client :refer [mysql-conn]]
            [yuppie-assessment.users.errors :as errors])
  (:import (com.mysql.jdbc.exceptions.jdbc4 MySQLIntegrityConstraintViolationException)))

(def profiles-table :user_profiles)

(defn- get-db [db] (-> mysql-conn db))

(defn get-user-profile-by-email
  "Get user profile by email from MySQL db"
  [db email]
  (let [result (jdbc/query (get-db db)
                           ["SELECT * FROM user_profiles WHERE email = ?" email]
                           {:result-set-fn first})]
    (when result (model/mysql-profile->profile result))))

(defn update-user-profile-by-email
  "Update user profile for email in MySQL db"
  [db email profile]
  (jdbc/update! (get-db db)
                profiles-table
                (model/profile->mysql-update profile)
                ["email = ?" email]))

(defn insert-user-profile
  "Insert user profile into MySQL db"
  [db profile]
  (try
    (jdbc/insert! (get-db db)
                  profiles-table
                  (model/profile->mysql-profile profile))
    (catch Exception e
      (if (instance? MySQLIntegrityConstraintViolationException e)
        (throw (ex-info errors/message-already-exists
                        {:type errors/type-already-exists
                         :profile profile}))
        (throw e)))))
