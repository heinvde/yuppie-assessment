(ns yuppie-assessment.users.rabbitmq.queues
  (:require [yuppie-assessment.rabbitmq.client :as rabbitmq]
            [yuppie-assessment.users.rabbitmq.consumers :as consumers]
            [mount.core :refer [defstate]]))

(declare user-profile-created-queue)

(defn- create-consumer [queue consumer]
  (-> queue
      :channel
      (rabbitmq/create-consumer (assoc consumer :queue queue))))

(defstate user-profile-created-queue
  :start (let [spec {:name "yuppie.users.profile-created.queue"
                     :opts {:auto-ack true
                            :durable true}
                     :exchange-name "yuppie.users.profile-created.exchange"
                     :exchange-type "fanout"
                     :publish-metadata {:content-type "application/json" :mandatory true}}
               channel rabbitmq/channel-default
               queue (assoc spec :channel channel)]
           (rabbitmq/create-queue channel spec)
           ; create queue consumers
           (create-consumer queue consumers/update-with-cloudinary-picture-consumer)
           queue))