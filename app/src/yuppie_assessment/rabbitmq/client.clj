(ns yuppie-assessment.rabbitmq.client
  (:require [langohr.core :as lcore]
            [langohr.channel :as lch]
            [langohr.queue :as lq]
            [langohr.consumers :as lcons]
            [langohr.basic :as lb]
            [langohr.exchange :as le]
            [mount.core :refer [defstate]]
            [clojure.data.json :as json]
            [yuppie-assessment.config :refer [config]]
            [yuppie-assessment.logger :refer [log-info]]))

(declare conn-default)
(declare channel-default)

(defstate conn-default
  :start (do
           (log-info "Creating RabbitMQ connection...")
           (log-info "RabbitMQ host: " (-> config :rabbitmq :default :host))
           (lcore/connect {:host (-> config :rabbitmq :default :host)
                           :port (-> config :rabbitmq :default :port Integer/parseInt)
                           :username (-> config :rabbitmq :default :username)
                           :password (-> config :rabbitmq :default :password)}))
  :stop (lcore/close conn-default))

(defstate channel-default
  :start (lch/open conn-default)
  :stop (lch/close channel-default))


(defn create-consumer [channel {:keys [handler queue opts]}]
  (lcons/subscribe channel
                   (:name queue)
                   handler
                   opts))

(defn create-queue [channel {:keys [name opts exchange-name exchange-type]}]
  (le/declare channel exchange-name exchange-type)
  (lq/declare channel name opts)
  (lq/bind channel name exchange-name))

(defn publish-message
  "Publish a message to the given queue."
  ([queue payload]
   (publish-message queue payload {}))
  ([queue payload metadata]
   (let [{:keys [name channel exchange-name publish-metadata]} queue]
     (lb/publish channel
                 exchange-name
                 name
                 payload
                 (merge publish-metadata metadata)))))

(defn publish-map
  "Publish a map to the given queue. The map will be converted to JSON."
  ([queue payload] (publish-map queue payload {}))
  ([queue payload metadata]
   (publish-message queue (json/write-str payload) metadata)))
