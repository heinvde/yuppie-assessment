(ns yuppie-assessment.rabbitmq.queues)

(defn health-check-consumer [channel metadata ^bytes payload] (println "I got message:" (String. payload)))

(def queues
  {:check {:name "yuppie.health-check.queue"
           :opts {:auto-ack true
                  :durable true}
           :exchange-name "yuppie.health-check.exchange"
           :exchange-type "fanout"
           :publish-metadata {:content-type "text/plain" :mandatory true}}})

(def consumers
  {:check {:handler health-check-consumer
           :queue :check
           :opts {:auto-ack true}}})
