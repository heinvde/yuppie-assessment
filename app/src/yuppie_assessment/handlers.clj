(ns yuppie-assessment.handlers
  (:require [clojure.data.json :as json]
            [ring.util.response :as response]
            [yuppie-assessment.google.client :as google]
            [yuppie-assessment.users.updates :as user-updates]
            [yuppie-assessment.users.queries :as user-queries]
            [yuppie-assessment.users.errors :as user-errors]
            [yuppie-assessment.config :refer [config]]))

(def internal-server-error
  (-> "Internal Server Error"
      (response/response)
      (response/content-type "text/plain")
      (response/status 500)))

(def unauthorized-error
  (-> "Unauthorized"
      (response/response)
      (response/content-type "text/plain")
      (response/status 401)))

(defn handle-health-check
  "Returns health check response."
  []
  (-> {:message "OK"}
      (json/write-str)
      (response/response)
      (response/content-type "application/json")
      (response/status 200)))

(defn handle-oauth2-redirect
  "Redirects to Google OAuth2 authentication."
  []
  (-> (google/get-oath2-request-url {:client-id (-> config :google :client-id)
                                     :redirect-uri (-> config :google :oauth2 :redirect-uri)
                                     :scopes [(google/scopes :user-email)
                                              (google/scopes :user-profile)]
                                     :state (-> config :google :oauth2 :state-key)})
      (response/redirect)))

(defn handle-oauth2-callback
  "Handles the OAuth2 callback from Google."
  [request]
  (letfn [(check-state-key [request google-config]
            (= (-> request :query-params (get "state"))
               (-> google-config :oauth2 :state-key)))
          (welcome-message [profile]
            (str "Welcome " (:first-name profile) " " (:last-name profile) ", your account has successfully been created."))
          (welcome-back-message [profile]
            (str "Welcome back " (:first-name profile) " " (:last-name profile) "."))
          (send-response [message]
            (-> message
                (response/response)
                (response/content-type "text/plain")
                (response/status 200)))
          (profile-exists-exception? [ex]
            (= (-> ex ex-data :type) user-errors/type-already-exists))
          (google-auth-exception? [ex]
            (= (-> ex ex-data :type) google/error-authentication))]
    (if (not (check-state-key request (:google config)))
      ; state key mismatch send 401
      (do (println "ERROR: Google OAuth2 state key mismatch.")
          unauthorized-error)
      (try
        (-> request
            :query-params
            (get "code")
            (user-updates/create-profile-with-google-oauth)
            (welcome-message)
            (send-response))
        (catch Exception ex
          (cond
            (google-auth-exception? ex) unauthorized-error
            (profile-exists-exception? ex) (-> (ex-data ex)
                                               :profile
                                               :email-address
                                               (user-queries/get-profile-by-email)
                                               (welcome-back-message)
                                               (send-response))
            :else (throw ex)))))))
