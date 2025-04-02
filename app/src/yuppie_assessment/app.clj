(ns yuppie-assessment.app
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defroutes app-routes
  (GET "/" [] "Not Implemented")
  (route/not-found "Not Found"))

(def app (wrap-defaults app-routes site-defaults))
