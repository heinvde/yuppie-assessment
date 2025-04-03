(ns yuppie-assessment.app
  (:require [clojure.string :as clj-string]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.util.response :as response]
            [yuppie-assessment.handlers :as handlers]
            [environ.core :refer [env]]))

(def required-env [:google-client-id
                   :google-client-secret
                   :google-oauth2-redirect-uri])
(def validate-env (complement
                   (fn [required-vars current-vars]
                     (some #(nil? (get current-vars %)) required-vars))))

(defn init-app
  "Called on initialization of App and used for logging and environment validation."
  []
  (println "Validating server startup...")
  (when (not (validate-env required-env env))
    (throw (ex-info
            (str "Missing one or more required environment variables: " (clj-string/join ", " required-env))
            {:required required-env})))
  (println "Validation successfull"))

(defroutes app-routes
  (GET "/" [] (response/redirect "/auth/verify"))
  (GET "/auth/verify" [] (handlers/handle-oauth2-redirect))
  (GET "/check" [] (handlers/handle-health-check))
  (route/not-found "Not Found"))

(def app (wrap-defaults app-routes site-defaults))
