(ns yuppie-assessment.users.rabbitmq.consumers
  (:require [clojure.data.json :as json]
            [yuppie-assessment.cloudinary.client :as cloudinary]
            [yuppie-assessment.users.repository.mysql :as repo]
            [yuppie-assessment.logger :refer [log-warning]]
            [yuppie-assessment.config :refer [config]]
            [yuppie-assessment.mysql.client :refer [user-db]]))

(defn- update-with-cloudinary-picture
  "Uploads the profile picture to Cloudinary and updates the user profile."
  [_ _ ^bytes payload]
  (let [{:keys [id profile-picture-url]} (-> payload ; get profile
                                             String.
                                             (json/read-str :key-fn keyword))
        cloudinary-config (:cloudinary config)
        {:keys [url]} (cloudinary/upload-image-from-url cloudinary-config profile-picture-url)]
    (if url
      (repo/update-user-profile-by-id user-db id {:profile-picture-url url})
      (log-warning "Failed to upload image to Cloudinary"))))

(def update-with-cloudinary-picture-consumer
  {:handler update-with-cloudinary-picture
   :opts {:auto-ack true}})
