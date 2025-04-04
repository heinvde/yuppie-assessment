(ns yuppie-assessment.google.client
  (:require [clojure.string :as clj-str]
            [clojure.data.json :as json]
            [ring.util.codec :as codec]
            [clj-http.client :as http]))

(def oauth2-response-type "code")
(def oauth2-access-type "offline")
(def scopes {:user-email "https://www.googleapis.com/auth/userinfo.email"
             :user-profile "https://www.googleapis.com/auth/userinfo.profile"})
(def token-grant-type "authorization_code")
(def json-key-fn (comp keyword #(clj-str/replace % #"_" "-")))

(def error-authentication :error-auth)
(def error-bad-request :error-bad-request)
(def error-unknown :error-unknown)

(defn- handle-http-exception [ex]
  (let [{:keys [status body]} (ex-data ex)
        throw-error (fn [ex type] (throw (ex-info
                                          (.getMessage ex)
                                          {:type type
                                           :body body
                                           :status status})))]
    (cond
      (nil? status) (throw ex) ; unkown exception
      (= status 400) (throw-error ex error-bad-request)
      (= status 401) (throw-error ex error-authentication)
      :else (throw-error ex error-unknown))))

(defn- map->query-string
  "Converts a map of key-value pairs to a query string."
  [query-param-map]
  (let [key-val->query (fn [[key val]]
                         (->> val
                              (codec/url-encode)
                              (str (name key) "=")))]
    (->> query-param-map
         (map key-val->query) ; format into "key=val"
         (clj-str/join "&"))))

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
                         :state (or state "none")}]
    (->> (map->query-string query-param-map)
         (str base-url "?"))))

(defn oauth2-code->access-token
  "Exchange the authorization code for an access token."
  ([{:keys [client-id client-secret code redirect-uri]}]
   {:pre [client-id client-secret code redirect-uri]
    :post [(:access-token %)]}
   (try
     (let [url "https://oauth2.googleapis.com/token"
           body {:code code
                 :client_id client-id
                 :client_secret client-secret
                 :redirect_uri redirect-uri
                 :grant_type token-grant-type}]
       (-> url
           (http/post {:form-params body})
           :body
           (json/read-str :key-fn json-key-fn)
           (select-keys [:access-token :expires-in :refresh-token])))
     (catch Exception ex (handle-http-exception ex))))
  ([client-spec code redirect-uri]
   (oauth2-code->access-token (assoc client-spec
                                     :redirect-uri redirect-uri
                                     :code code))))

(defn- grab-profile-attribute
  "Get the primary (or default) attribute value of a field from the GET profile API response. For example, field would be `names` and attribute `givenName`."
  [profile field attribute]
  (as-> profile $
    (get $ field)
    (filter #(-> % :metadata :primary) $) ; get primary objects for field
    (first $)
    (get $ attribute)
    (or $
        (-> profile field first attribute)))) ; if no primary, get first object

(defn- grab-user-profile
  "Get the user profile from the GET profile API response."
  [profile]
  (array-map :first-name (grab-profile-attribute profile :names :givenName)
             :last-name (grab-profile-attribute profile :names :familyName)
             :email-address (grab-profile-attribute profile :emailAddresses :value)
             :profile-picture-url (grab-profile-attribute profile :photos :url)))

(defn get-user-profile
  "Get google profile of user using access token."
  [{:keys [access-token]}]
  {:pre [access-token]}
  (try
    (let [url "https://people.googleapis.com/v1/people/me"
          person-fields ["names"
                         "photos"
                         "emailAddresses"]
          query-param-map {:personFields (clj-str/join "," person-fields)}
          query-string (map->query-string query-param-map)
          headers {"Authorization" (str "Bearer " access-token)}]
      (-> (str url "?" query-string)
          (http/get {:headers headers})
          :body
          (json/read-str :key-fn json-key-fn)
          (grab-user-profile)))
    (catch Exception ex (handle-http-exception ex))))
