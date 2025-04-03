(ns yuppie-assessment.app
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.util.response :as response]
            [yuppie-assessment.handlers :as handlers]))

(defroutes app-routes
  (GET "/" [] (response/redirect "/auth/verify"))
  (GET "/auth/verify" [] (handlers/handle-oauth2-redirect))
  (GET "/check" [] (handlers/handle-health-check))
  (route/not-found "Not Found"))

(def app (wrap-defaults app-routes site-defaults))
