(ns yuppie-assessment.users.rabbitmq.consumers
  (:require [clojure.data.json :as json]
            [yuppie-assessment.users.repository.mysql :as repo]
            [yuppie-assessment.users.rabbitmq.queues :as q]
            [yuppie-assessment.users.updates :as user-updates]
            [yuppie-assessment.mysql.client :refer [user-db]]
            [yuppie-assessment.rabbitmq.client :as rabbitmq]
            [mount.core :refer [defstate]]))

(declare update-with-cloudinary-picture-consumer)

(defn- update-with-cloudinary-picture
  "Uploads the profile picture to Cloudinary and updates the user profile."
  [_ _ ^bytes payload]
  (let [{:keys [id]} (-> payload ; get profile
                         String.
                         (json/read-str :key-fn keyword))
        {:keys [url]} (user-updates/upload-profile-picture-to-cloudinary id)]
    (repo/update-user-profile-by-id user-db id {:profile-picture-url url})))

(defstate update-with-cloudinary-picture-consumer
  :start (let [queue q/user-profile-created-queue
               consumer {:handler update-with-cloudinary-picture
                         :queue queue
                         :opts {:auto-ack true}}]
           (-> queue
               :channel
               (rabbitmq/create-consumer consumer))))
