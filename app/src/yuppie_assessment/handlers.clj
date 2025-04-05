(ns yuppie-assessment.handlers
  (:require [clojure.data.json :as json]
            [ring.util.response :as response]
            [yuppie-assessment.google.client :as google]
            [yuppie-assessment.users.updates :as user-updates]
            [yuppie-assessment.users.queries :as user-queries]
            [yuppie-assessment.users.errors :as user-errors]
            [environ.core :refer [env]]))

(def internal-server-error
  (-> "Internal Server Error"
      (response/response)
      (response/content-type "text/plain")
      (response/status 500)))

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
  (-> (google/get-oath2-request-url {:client-id (env :google-client-id)
                                     :redirect-uri (env :google-oauth2-redirect-uri)
                                     :scopes [(google/scopes :user-email)
                                              (google/scopes :user-profile)]
                                     :state (env :google-oauth2-state-key)})
      (response/redirect)))

(defn handle-oauth2-callback
  "Handles the OAuth2 callback from Google."
  [request]
  (try
    (let [profile (-> request
                      :query-params
                      (get "code")
                      (user-updates/create-profile-with-google-oauth))]
      (-> (str "Welcome " (:first-name profile) " " (:last-name profile) ", your account has successfully been created.")
          (response/response)
          (response/status 200)
          (response/content-type "text/plain")))
    (catch Exception ex
      (if (= (-> ex ex-data :type) user-errors/type-already-exists)
        (if-let [profile (user-queries/get-profile-by-email (-> ex ex-data :profile :email-address))]
          (-> (str "Welcome back " (:first-name profile) " " (:last-name profile) ".")
              (response/response)
              (response/status 200)
              (response/content-type "text/plain"))
          internal-server-error)
        internal-server-error))))
