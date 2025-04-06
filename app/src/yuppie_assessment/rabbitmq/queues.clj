(ns yuppie-assessment.rabbitmq.queues)

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
