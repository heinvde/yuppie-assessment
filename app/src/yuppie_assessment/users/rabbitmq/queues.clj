(ns yuppie-assessment.users.rabbitmq.queues
  (:require [yuppie-assessment.rabbitmq.client :as rabbitmq]
            [mount.core :refer [defstate]]))

(declare user-profile-created-queue)

(defstate user-profile-created-queue
  :start (let [queue {:name "yuppie.users.profile-created.queue"
                      :opts {:auto-ack true
                             :durable true}
                      :exchange-name "yuppie.users.profile-created.exchange"
                      :exchange-type "fanout"
                      :publish-metadata {:content-type "application/json" :mandatory true}}]
           (rabbitmq/create-queue rabbitmq/channel-default queue)
           (assoc queue :channel rabbitmq/channel-default)))