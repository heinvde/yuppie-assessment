(ns yuppie-assessment.rabbitmq.queues
  (:require [yuppie-assessment.users.rabbitmq.consumers :as user-consumers]))

(def profile-created-queue :profile-created-queue)

(def queues
  (array-map
   :check {:name "yuppie.health-check.queue"
           :opts {:auto-ack true
                  :durable true}
           :exchange-name "yuppie.health-check.exchange"
           :exchange-type "fanout"
           :publish-metadata {:content-type "text/plain" :mandatory true}}
   profile-created-queue {:name "yuppie.users.profile-created.queue"
                          :opts {:auto-ack true
                                 :durable true}
                          :exchange-name "yuppie.users.profile-created.exchange"
                          :exchange-type "fanout"
                          :publish-metadata {:content-type "application/json" :mandatory true}}))

(defn health-check-consumer [_ _ ^bytes payload] (println "I got oh yes message:" (String. payload)))

(def consumers
  {:check {:handler health-check-consumer
           :queue :check
           :opts {:auto-ack true}}
   :upload-profile-picture {:handler user-consumers/upload-profile-picture
                            :queue profile-created-queue
                            :opts {:auto-ack true}}})
