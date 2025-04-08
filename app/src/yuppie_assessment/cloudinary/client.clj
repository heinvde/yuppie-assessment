(ns yuppie-assessment.cloudinary.client
  (:require [clojure.string :as clj-str]
            [yuppie-assessment.logger :refer [log-error]])
  (:import (com.cloudinary Cloudinary)))

(def error-unauthorized :error-unauthorized)
(def error-resource-not-found :error-resource-not-found)
(def error-cloudname-not-found :error-cloudname-not-found)

(defn- handle-exception [ex]
  (let [message (.getMessage ex)]
    (log-error "Cloudinary error:" message)
    (cond
      (clj-str/includes? message "Resource not found")
      (throw (ex-info message {:type error-resource-not-found}))
      (clj-str/includes? message "Invalid api_key")
      (throw (ex-info message {:type error-unauthorized}))
      (clj-str/includes? message "Invalid cloud_name")
      (throw (ex-info message {:type error-cloudname-not-found}))
      (clj-str/includes? message "Invalid Signature")
      (throw (ex-info message {:type error-unauthorized}))
      :else (throw ex)))) ;unkown error

(defn- format-upload-response [response]
  {:url (get response "secure_url")
   :display-name (get response "display_name")
   :asset-id (get response "asset_id")
   :public-id (get response "public_id")})

(defn upload-image-from-url
  ([client-spec url] (upload-image-from-url client-spec url {}))
  ([client-spec url settings]
   (try
     (some-> client-spec
             :url
             (Cloudinary.)
             (.uploader)
             (.upload url (merge {"resource_type" "image" "overwrite" true "use_name" true "timeout" 1}
                                 settings))
             (format-upload-response))
     (catch Exception e
       (handle-exception e)))))
