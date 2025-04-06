(ns yuppie-assessment.users.rabbitmq.consumers
  (:require [clojure.data.json :as json]
            [yuppie-assessment.cloudinary.client :as cloudinary]
            [yuppie-assessment.users.repository.mysql :as repo]
            [yuppie-assessment.mysql.client :refer [user-db]]
            [yuppie-assessment.config :refer [config]]
            [clojure.string :as clj-str]))

(defn upload-profile-picture
  "Uploads the profile picture to Cloudinary and updates the user profile."
  [_ _ ^bytes payload]
  (let [{:keys [id profile-picture-url]} (-> payload ; get profile
                                             String.
                                             (json/read-str :key-fn keyword))]
    ; Should not already be uploaded to cloudinary
    (when (not (clj-str/includes? profile-picture-url "https://res.cloudinary.com"))
      (let [{:keys [url]} (cloudinary/upload-image-from-url (:cloudinary config) profile-picture-url)]
        (repo/update-user-profile-by-id user-db
                                        id
                                        {:profile-picture-url url})))))
