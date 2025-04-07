(ns yuppie-assessment.rabbitmq.client-integration-test
  (:require [clojure.test :refer :all]
            [mount.core :as mount]
            [yuppie-assessment.config]
            [yuppie-assessment.rabbitmq.client :as client]
            [yuppie-assessment.rabbitmq.queues :refer [health-check-queue]]))

(use-fixtures :once
  (fn [run-tests]
    (mount/start #'yuppie-assessment.config/config
                 #'yuppie-assessment.rabbitmq.queues/health-check-queue
                 #'yuppie-assessment.rabbitmq.client/conn-default
                 #'yuppie-assessment.rabbitmq.client/channel-default)
    (run-tests)
    (mount/stop)))


(deftest ^:integration can-publish-health-check
  (testing "can publish health check message"
    (client/publish-message health-check-queue "Ok!")
    (Thread/sleep 10)
    (is true))
  (testing "can publish health check message with metadata"
    (client/publish-message health-check-queue "Ok!" {:message-id "myid!"})
    (Thread/sleep 10)
    (is true)))
