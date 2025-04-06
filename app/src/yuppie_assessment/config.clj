(ns yuppie-assessment.config
  (:require [mount.core :refer [defstate]]
            [environ.core :refer [env]]))

(declare config)
(defstate config
  :start {:mysql {:users {:host (env :mysql-users-host)
                          :port (env :mysql-users-port)
                          :user (env :mysql-users-user)
                          :password (env :mysql-users-password)
                          :db-name (env :mysql-users-db-name)}}
          :google {:client-id (env :google-client-id)
                   :client-secret (env :google-client-secret)
                   :oauth2 {:redirect-uri (env :google-oauth2-redirect-uri)
                            :state-key (env :google-oauth2-state-key)}}
          :rabbitmq {:default {:host (env :rabbitmq-default-host)
                               :port (env :rabbitmq-default-port)
                               :username (env :rabbitmq-default-username)
                               :password (env :rabbitmq-default-password)}}
          :cloudinary {:url (env :cloudinary-url)}})
