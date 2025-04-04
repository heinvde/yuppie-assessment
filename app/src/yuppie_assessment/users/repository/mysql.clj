(ns yuppie-assessment.users.repository.mysql
  (:require [clojure.java.jdbc :as jdbc]
            [yuppie-assessment.users.model.profile :as model]
            [yuppie-assessment.mysql.client :refer [mysql-conn]]))

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
  (jdbc/insert! (get-db db)
                profiles-table
                (model/profile->mysql-profile profile)))
