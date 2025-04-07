(ns yuppie-assessment.cloudinary.client
  (:import (com.cloudinary Cloudinary)))

(defn- format-upload-response [response]
  {:url (get response "secure_url")
   :display-name (get response "display_name")
   :asset-id (get response "asset_id")
   :public-id (get response "public_id")})

(defn upload-image-from-url
  ([client-spec url] (upload-image-from-url client-spec url {}))
  ([client-spec url settings]
   (some-> client-spec
           :url
           (Cloudinary.)
           (.uploader)
           (.upload url (merge {"resource_type" "image" "overwrite" true "use_name" true}
                               settings))
           (format-upload-response))))
