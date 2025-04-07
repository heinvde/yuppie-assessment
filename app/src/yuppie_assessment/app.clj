(ns yuppie-assessment.app
  (:require [clojure.string :as clj-string]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.util.response :as response]
            [yuppie-assessment.handlers :as handlers]
            [mount.core :as mount]
            [environ.core :refer [env]]
            [yuppie-assessment.logger :refer [log-info log-error]]))

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
  (GET "/users/:id/profile" request (handlers/handle-get-user-profile request))
  (route/not-found "Not Found"))

(defn init-app
  "Called on initialization to mount app state."
  []
  (log-info "Validating server startup...")
  (when (not (validate-env required-env env))
    (throw (ex-info
            (str "Missing one or more required environment variables: " (clj-string/join ", " required-env))
            {:required required-env})))
  (log-info "Validation successfull")
  (log-info "Mounting state...")
  (mount/start)
  (log-info "Done."))

(defn shutdown-app
  "Called on shutdown of to disconnect app state."
  []
  (log-info "Shutting down...")
  (mount/stop)
  (log-info "Done."))

(defn error-handler [handler]
  (fn [request]
    (try
      (handler request)
      (catch Exception ex
        (log-error ex)
        handlers/internal-server-error))))

(def app (-> app-routes
          (wrap-defaults site-defaults)
          (error-handler)))
