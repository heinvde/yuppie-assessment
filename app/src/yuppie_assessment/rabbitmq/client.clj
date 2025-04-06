(ns yuppie-assessment.rabbitmq.client
  (:require [langohr.core :as lcore]
            [langohr.channel :as lch]
            [langohr.queue :as lq]
            [langohr.consumers :as lcons]
            [langohr.basic :as lb]
            [langohr.exchange :as le]
            [mount.core :refer [defstate]]
            [clojure.data.json :as json]
            [yuppie-assessment.rabbitmq.queues :refer [queues consumers]]
            [yuppie-assessment.config :refer [config]]))

(declare conn-default)
(declare channel-default)

(defn- create-consumer [channel {:keys [handler queue opts]}]
  (lcons/subscribe channel
                   (-> queues (get queue) :name)
                   handler
                   opts))

(defn- create-queue [channel queue]
  (let [{:keys [name
                opts
                exchange-name
                exchange-type]} queue]
    (le/declare channel exchange-name exchange-type)
    (lq/declare channel name opts)
    (lq/bind channel name exchange-name)
    queue))

(defn- create-queues
  "Create queues and exchanges in RabbitMQ for defined queues map."
  [channel]
  (loop [queues-kv (seq queues)]
    (when-let [[_ queue] (first queues-kv)]
      (create-queue channel queue)
      (recur (rest queues-kv)))))

(defn- create-consumers
  "Create consumers in RabbitMQ for defined consumer map."
  [channel]
  (loop [consumers-kv (seq consumers)]
    (when-let [[_ consumer] (first consumers-kv)]
      (create-consumer channel consumer)
      (recur (rest consumers-kv)))))

(defstate conn-default
  :start (do
           (println "Creating RabbitMQ connection...")
           (println "RabbitMQ host: " (-> config :rabbitmq :default :host))
           (lcore/connect {:host (-> config :rabbitmq :default :host)
                           :port (-> config :rabbitmq :default :port Integer/parseInt)
                           :username (-> config :rabbitmq :default :username)
                           :password (-> config :rabbitmq :default :password)}))
  :stop (lcore/close conn-default))

(defstate channel-default
  :start (let [channel (lch/open conn-default)]
           (create-queues channel)
           (create-consumers channel)
           channel)
  :stop (lch/close channel-default))

(defn publish-message
  "Publish a message to the given queue."
  ([queue payload]
   (publish-message queue payload {}))
  ([queue payload metadata]
   (let [{:keys [name exchange-name publish-metadata]} (get queues queue)]
     (lb/publish channel-default
                 exchange-name
                 name
                 payload
                 (merge publish-metadata metadata)))))

(defn publish-map
  "Publish a map to the given queue. The map will be converted to JSON."
  ([queue payload] (publish-map queue payload {}))
  ([queue payload metadata]
   (publish-message queue (json/write-str payload) metadata)))
