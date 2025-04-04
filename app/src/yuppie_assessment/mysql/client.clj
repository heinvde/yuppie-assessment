(ns yuppie-assessment.mysql.client
  (:require [mount.core :refer [defstate]]
            [yuppie-assessment.config :refer [config]])
  (:import (com.mchange.v2.c3p0 ComboPooledDataSource)))

(def user-db :mysql-users)

(defn- pool
  [spec]
  (let [data-source (doto (ComboPooledDataSource.)
                      (.setDriverClass (:classname spec))
                      (.setJdbcUrl (str "jdbc:" (:subprotocol spec) ":" (:subname spec)))
                      (.setUser (:user spec))
                      (.setPassword (:password spec))
                      (.setMaxIdleTimeExcessConnections (* 30 60))
                      (.setMaxIdleTime (* 3 60 60)))]
    {:datasource data-source}))

(declare mysql-conn)
(defstate mysql-conn
  :start (let [with-host #(str "//" (:host %) ":" (:port %))
               with-db #(str "/" (:db-name %))
               with-params #(str "?useSSL=" (:use-ssl %))
               db->pool (fn [db-spec] {:classname "com.mysql.jdbc.Driver"
                                       :subprotocol "mysql"
                                       :subname (str (with-host db-spec)
                                                     (with-db db-spec)
                                                     (with-params {:use-ssl false}))
                                       :user (:user db-spec)
                                       :password (:password db-spec)})]
           (array-map :mysql-users
                      (-> config :mysql :users db->pool pool)))
  :stop (when-let [ds (-> mysql-conn user-db :datasource)]
          (.close ds)))
