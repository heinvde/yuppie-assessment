(ns yuppie-assessment.handlers
  (:require [clojure.data.json :as json]
            [ring.util.response :as response]
            [yuppie-assessment.logger :refer [log-warning]]
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

(defn- welcome-message [profile]
  (str "<h2>Welcome " (:first-name profile) " " (:last-name profile)"</h2>"
       "Your account has successfully been created. "
       "We are busy uploading your profile picture to Cloudinary. "
       "Visit <a target=\"_blank\" href=\"/users/" (:id profile) "/profile\">this URL</a> "
       "to see when your profile picture URL has been uploaded."))

(defn- welcome-back-message [profile]
  (str "<h2>Welcome back " (:first-name profile) " " (:last-name profile) "</h2>"
       "Visit <a target=\"_blank\" href=\"/users/" (:id profile) "/profile\">this URL</a> "
       "to see your profile data."))

(defn handle-oauth2-callback
  "Handles the OAuth2 callback from Google."
  [request]
  (letfn [(check-state-key [request google-config]
            (= (-> request :query-params (get "state"))
               (-> google-config :oauth2 :state-key)))
          (send-response [message]
            (-> message
                (response/response)
                (response/content-type "text/html")
                (response/status 200)))
          (profile-exists-exception? [ex]
            (= (-> ex ex-data :type) user-errors/type-already-exists))
          (google-auth-exception? [ex]
            (= (-> ex ex-data :type) google/error-authentication))
          (google-request-exception? [ex]
            (= (-> ex ex-data :type) google/error-bad-request))]

    (if (not (check-state-key request (:google config)))
      ; state key mismatch send 401
      (do (log-warning "Google OAuth2 state key mismatch. This might be an attack on the server.")
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
            (google-request-exception? ex) (response/redirect "/auth/verify")
            :else (throw ex)))))))

(defn handle-get-user-profile
  "Handles the GET request for user profile."
  [request]
  (if-let [profile (-> request
                       :params
                       :id
                       (user-queries/get-profile-by-id))]
    (-> profile
        (json/write-str :escape-slash false)
        (response/response)
        (response/content-type "application/json")
        (response/status 200))
    (-> {:message "User not found"}
        (json/write-str)
        (response/response)
        (response/content-type "application/json")
        (response/status 404))))
