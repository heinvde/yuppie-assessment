(ns yuppie-assessment.users.repository.mysql
  (:require [clojure.java.jdbc :as jdbc]
            [yuppie-assessment.users.model.profile :as model]
            [yuppie-assessment.mysql.client :refer [mysql-conn]]
            [yuppie-assessment.users.errors :as errors])
  (:import (com.mysql.jdbc.exceptions.jdbc4 MySQLIntegrityConstraintViolationException)))

(def profiles-table :user_profiles)

(defn- get-db [db] (-> mysql-conn db))

(defn update-user-by-query
  "Update user profile for query in MySQL db"
  [db profile query-vector]
  (jdbc/update! (get-db db)
                profiles-table
                (-> profile
                    (assoc :date-updated (java.util.Date.))
                    (model/profile->mysql-update))
                query-vector))

(defn update-user-profile-by-id
  "Update user profile for id in MySQL db"
  [db id profile]
  (update-user-by-query db profile ["id = ?" id]))

(defn update-user-profile-by-email
  "Update user profile for email in MySQL db"
   [db email profile]
   (update-user-by-query db profile ["email = ?" email]))

(defn insert-user-profile
  "Insert user profile into MySQL db"
  [db profile]
  (try
    (jdbc/insert! (get-db db)
                  profiles-table
                  (-> profile
                      (assoc :date-created (java.util.Date.))
                      (assoc :date-updated (java.util.Date.))
                      (model/profile->mysql-profile)))
    (catch Exception e
      (if (instance? MySQLIntegrityConstraintViolationException e)
        (throw (ex-info errors/message-already-exists
                        {:type errors/type-already-exists
                         :profile profile}))
        (throw e)))))

(defn get-user-profile-by-email
  "Get user profile by email from MySQL db"
  [db email]
  (let [result (jdbc/query (get-db db)
                           ["SELECT * FROM user_profiles WHERE email = ?" email]
                           {:result-set-fn first})]
    (when result (model/mysql-profile->profile result))))

(defn get-user-profile-by-id
  "Get user profile by id from MySQL db"
  [db id]
  (let [result (jdbc/query (get-db db)
                           ["SELECT * FROM user_profiles WHERE id = ?" id]
                           {:result-set-fn first})]
    (when result (model/mysql-profile->profile result))))
