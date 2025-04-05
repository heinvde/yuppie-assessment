(ns yuppie-assessment.users.queries
  (:require [yuppie-assessment.users.repository.mysql :as mysql-repo]
            [yuppie-assessment.mysql.client :refer [user-db]]))

(defn get-user-profile-by-email
  "Get user profile by email"
  [email]
  (mysql-repo/get-user-profile-by-email user-db email))
