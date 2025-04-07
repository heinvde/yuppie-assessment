(ns yuppie-assessment.rabbitmq.queues
  (:require [yuppie-assessment.rabbitmq.client :as rabbitmq]
            [mount.core :refer [defstate]]))

(declare health-check-queue)

(defstate health-check-queue
  :start (let [channel rabbitmq/channel-default
               queue {:name "yuppie.health-check.queue"
                      :opts {:auto-ack true
                             :durable true}
                      :exchange-name "yuppie.health-check.exchange"
                      :exchange-type "fanout"
                      :publish-metadata {:content-type "text/plain" :mandatory true}}]
           (rabbitmq/create-queue channel queue)
           (assoc queue :channel channel)))
