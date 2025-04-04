(ns yuppie-assessment.config
  (:require [mount.core :refer [defstate]]
            [environ.core :refer [env]]))

(declare config)
(defstate config
  :start {:mysql {:users {:host (env :mysql-users-host)
                          :port (env :mysql-users-port)
                          :user (env :mysql-users-user)
                          :password (env :mysql-users-password)
                          :db-name (env :mysql-users-db-name)}}})
