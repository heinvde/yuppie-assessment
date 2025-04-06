(ns yuppie-assessment.users.rabbitmq.consumers
  (:require [clojure.data.json :as json]
            [yuppie-assessment.cloudinary.client :as cloudinary]
            [yuppie-assessment.users.updates :as user-updates]
            [yuppie-assessment.config :refer [config]]))

(defn upload-profile-picture
  "Uploads the profile picture to Cloudinary and updates the user profile."
  [_ _ ^bytes payload]
  (let [profile (-> payload
                    String.
                    (json/read-str :key-fn keyword))
        {:keys [url]} (cloudinary/upload-image-from-url (:cloudinary config)
                                                        (:profile-picture-url profile))]
    (user-updates/update-profile-by-id (:id profile)
                                       {:profile-picture-url url})))
