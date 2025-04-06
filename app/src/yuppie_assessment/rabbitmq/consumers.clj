(ns yuppie-assessment.rabbitmq.consumers
  (:require [yuppie-assessment.users.rabbitmq.consumers :as user-consumers]
            [yuppie-assessment.rabbitmq.queues :refer [profile-created-queue]]))

(defn health-check-consumer [_ _ ^bytes payload] (println "I got oh yes message:" (String. payload)))

(def consumers
  {:check {:handler health-check-consumer
           :queue :check
           :opts {:auto-ack true}}

   :upload-profile-picture {:handler user-consumers/upload-profile-picture
                            :queue profile-created-queue
                            :opts {:auto-ack true}}})
