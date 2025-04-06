(ns yuppie-assessment.users.rabbitmq.consumers
  (:require [clojure.data.json :as json]
            [yuppie-assessment.cloudinary.client :as cloudinary]
            [yuppie-assessment.users.repository.mysql :as repo]
            [yuppie-assessment.mysql.client :refer [user-db]]
            [yuppie-assessment.config :refer [config]]))

(defn upload-profile-picture
  "Uploads the profile picture to Cloudinary and updates the user profile."
  [_ _ ^bytes payload]
  (let [profile (-> payload
                    String.
                    (json/read-str :key-fn keyword))
        {:keys [url]} (cloudinary/upload-image-from-url (:cloudinary config)
                                                        (:profile-picture-url profile))]
    (repo/update-user-profile-by-id user-db
                                    (:id profile)
                                    {:profile-picture-url url})))
