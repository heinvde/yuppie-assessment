(ns yuppie-assessment.rabbitmq.client-integration-test
  (:require [clojure.test :refer :all]
            [mount.core :as mount]
            [yuppie-assessment.config]
            [yuppie-assessment.rabbitmq.client :as client]
            [langohr.queue :as lq]
            [yuppie-assessment.rabbitmq.queues :refer [queues health-check-consumer]]))

(deftest ^:integration can-publish-health-check
  (mount/start #'yuppie-assessment.config/config
               #'yuppie-assessment.rabbitmq.client/conn-default
               #'yuppie-assessment.rabbitmq.client/channel-default)
  (testing "can subscribe consumers"
    (let [qname (-> queues :check :name)
          qstatus (lq/status client/channel-default qname)]
      (is (> (:consumer-count qstatus) 0))))
  (testing "can publish health check message"
    (client/publish-message :check "Ok!")
    (Thread/sleep 10)
    (is true))
  (testing "can publish health check message with metadata"
    (client/publish-message :check "Ok!" {:message-id "myid!"})
    (Thread/sleep 10)
    (is true))
  (mount/stop))
