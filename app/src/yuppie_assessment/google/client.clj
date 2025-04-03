(ns yuppie-assessment.google.client
  (:require [clojure.string :as clj-str]
            [ring.util.codec :as codec]))

(def oauth2-response-type "code")
(def oauth2-access-type "offline")
(def scopes {:user-email "https://www.googleapis.com/auth/userinfo.email"
             :user-profile "https://www.googleapis.com/auth/userinfo.profile"})

(defn get-oath2-request-url
  "Returns the URL to redirect to for Google OAuth2 authentication."
  [{:keys [client-id redirect-uri scopes state]}]
  {:pre [client-id redirect-uri (vector? scopes)]}
  (let [base-url "https://accounts.google.com/o/oauth2/v2/auth"
        query-param-map {:client_id client-id
                         :redirect_uri redirect-uri
                         :response_type oauth2-response-type
                         :scope (clj-str/join " " scopes)
                         :access_type oauth2-access-type
                         :include_granted_scopes "true"
                         :state (or state "none")}
        key-val->query (fn [[key val]]
                         (->> val
                              (codec/url-encode)
                              (str (name key) "=")))]
    (->> query-param-map
         (map key-val->query) ; format into "key=val"
         (clj-str/join "&")
         (str base-url "?"))))
