(ns yuppie-assessment.mysql.client-test
  (:require [clojure.test :refer :all]
            [yuppie-assessment.config]
            [yuppie-assessment.mysql.client :refer [mysql-conn user-db]]
            [mount.core :as mount]))

(deftest test-mount-connections
  (testing "can mount connection pool"
    (mount/start #'yuppie-assessment.config/config
                 #'yuppie-assessment.mysql.client/mysql-conn)
    (is (not (nil? (-> mysql-conn user-db :datasource))))
    (mount/stop)))
