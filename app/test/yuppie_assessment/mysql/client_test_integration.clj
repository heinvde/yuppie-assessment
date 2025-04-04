(ns yuppie-assessment.mysql.client-test-integration
  (:require [clojure.test :refer :all]
            [yuppie-assessment.config]
            [clojure.java.jdbc :as jdbc]
            [yuppie-assessment.mysql.client :refer [mysql-conn user-db]]
            [mount.core :as mount]))

(use-fixtures :once
  (fn [run-tests]
    (mount/start #'yuppie-assessment.config/config
                 #'yuppie-assessment.mysql.client/mysql-conn)
    (run-tests)
    (mount/stop)))

(deftest ^:integration test-connection-query
  (testing "can make query with connection pool"
    (is (= '({:value 1})
           (jdbc/query (mysql-conn user-db) ["SELECT 1 AS value"])))))
