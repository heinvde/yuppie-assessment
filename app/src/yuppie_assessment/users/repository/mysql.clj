(ns yuppie-assessment.users.repository.mysql
  (:require [clojure.java.jdbc :as jdbc]
            [yuppie-assessment.users.model.profile :as model]
            [yuppie-assessment.mysql.client :refer [mysql-conn]]))

(def profiles-table :user_profiles)

(defn- get-db [db] (-> mysql-conn db))

(defn insert-user-profile
  "Insert user profile into MySQL db"
  [db profile]
  (jdbc/insert! (get-db db)
                profiles-table
                (model/profile->mysql-profile profile)))
