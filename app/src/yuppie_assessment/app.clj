(ns yuppie-assessment.app
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [yuppie-assessment.handlers :as handlers]))

(defroutes app-routes
  (GET "/" [] "Not Implemented")
  (GET "/auth/verify" [] (handlers/handle-oauth2-redirect))
  (GET "/check" [] (handlers/handle-health-check))
  (route/not-found "Not Found"))

(def app (wrap-defaults app-routes site-defaults))
