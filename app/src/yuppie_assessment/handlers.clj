(ns yuppie-assessment.handlers
  (:require [clojure.data.json :as json]
            [ring.util.response :as response]))

(defn handle-health-check []
  (-> {:message "OK"}
      (json/write-str)
      (response/response)
      (response/content-type "application/json")
      (response/status 200)))
