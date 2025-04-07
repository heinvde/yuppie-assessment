(ns yuppie-assessment.rabbitmq.consumers
  (:require [yuppie-assessment.rabbitmq.queues :refer [health-check-queue]]
            [yuppie-assessment.rabbitmq.client :as rabbitmq]
            [yuppie-assessment.logger :refer [log-info]]
            [mount.core :refer [defstate]]))

(declare health-check-consumer)

(defstate health-check-consumer
  :start (let [handler (fn [_ _ ^bytes payload]
                         (log-info "I got the message:" (String. payload)))
               consumer {:handler handler
                         :queue health-check-queue
                         :opts {:auto-ack true}}]
           (-> health-check-queue
               :channel
               (rabbitmq/create-consumer consumer))))

