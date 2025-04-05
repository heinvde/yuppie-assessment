(ns yuppie-assessment.app
  (:require [clojure.string :as clj-string]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.util.response :as response]
            [yuppie-assessment.handlers :as handlers]
            [mount.core :as mount]
            [environ.core :refer [env]]))

(def required-env [:google-client-id
                   :google-client-secret
                   :google-oauth2-redirect-uri])
(def validate-env (complement
                   (fn [required-vars current-vars]
                     (some #(nil? (get current-vars %)) required-vars))))

(defroutes app-routes
  (GET "/" [] (response/redirect "/auth/verify"))
  (GET "/auth/verify" [] (handlers/handle-oauth2-redirect))
  (GET "/auth/verified" request (handlers/handle-oauth2-callback request))
  (GET "/check" [] (handlers/handle-health-check))
  (route/not-found "Not Found"))

(defn init-app
  "Called on initialization of App and used for logging and environment validation."
  []
  (println "Validating server startup...")
  (when (not (validate-env required-env env))
    (throw (ex-info
            (str "Missing one or more required environment variables: " (clj-string/join ", " required-env))
            {:required required-env})))
  (println "Validation successfull")
  (println "Mounting state...")
  (mount/start)
  (println "Done."))

(defn shutdown-app
  "Called on shutdown of App and used for logging and environment validation."
  []
  (println "Shutting down...")
  (mount/stop)
  (println "Done."))

(defn error-handler [handler]
  (fn [request]
    (try
      (handler request)
      (catch Exception ex
        (println ex)
        handlers/internal-server-error))))

(def app (-> app-routes
          (wrap-defaults site-defaults)
          (error-handler)))
