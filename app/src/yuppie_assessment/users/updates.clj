(ns yuppie-assessment.users.updates
  (:require [yuppie-assessment.google.client :as google]
            [yuppie-assessment.config :refer [config]]
            [yuppie-assessment.users.repository.mysql :as mysql-repo]
            [yuppie-assessment.mysql.client :refer [user-db]]))

(defn create-user-with-google-oauth
  "Create a new user with Google OAuth code"
  [oauth-code]
  (let [client-spec (config :google)
        redirect-uri (-> client-spec :oauth2 :redirect-uri)
        profile-id (random-uuid)
        profile (-> (google/oauth2-code->access-token client-spec oauth-code redirect-uri)
                    (google/get-user-profile)
                    (assoc :id profile-id))]
    (if-let [found (mysql-repo/get-user-profile-by-email user-db (:email-address profile))]
      (do (mysql-repo/update-user-profile-by-email user-db (:email-address profile) profile)
          (assoc profile :id (:id found)))
      (do (mysql-repo/insert-user-profile user-db profile)
          profile))))