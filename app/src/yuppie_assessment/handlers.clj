(ns yuppie-assessment.handlers
  (:require [clojure.data.json :as json]
            [ring.util.response :as response]
            [yuppie-assessment.google.client :as google]
            [environ.core :refer [env]]))

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
