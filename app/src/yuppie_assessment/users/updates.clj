(ns yuppie-assessment.users.updates
  (:require [yuppie-assessment.google.client :as google]
            [yuppie-assessment.config :refer [config]]
            [yuppie-assessment.users.repository.mysql :as mysql-repo]
            [yuppie-assessment.rabbitmq.client :as rmq-client]
            [yuppie-assessment.users.rabbitmq.queues :as q]
            [yuppie-assessment.mysql.client :refer [user-db]]
            [yuppie-assessment.cloudinary.client :as cloudinary]))

(defn create-profile
  "Create a new user profile"
  [profile]
  (let [profile-id (-> random-uuid str)
        profile (assoc profile :id profile-id)]
    (mysql-repo/insert-user-profile user-db profile)
    (rmq-client/publish-map q/user-profile-created-queue profile)
    profile))

(defn create-profile-with-google-oauth
  "Create a new user with Google OAuth code"
  [oauth-code]
  (let [client-spec (config :google)
        redirect-uri (-> client-spec :oauth2 :redirect-uri)]
    (-> (google/oauth2-code->access-token client-spec oauth-code redirect-uri)
        (google/get-user-profile)
        (create-profile))))

(defn upload-profile-picture-to-cloudinary
  "Uploads the profile picture to Cloudinary."
  [id]
  (when-let [{:keys [profile-picture-url]} (mysql-repo/get-user-profile-by-id user-db id)]
    (cloudinary/upload-image-from-url (:cloudinary config)
                                      profile-picture-url)))
