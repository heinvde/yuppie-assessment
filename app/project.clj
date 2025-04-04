(defproject yuppie-assessment "0.1.0-SNAPSHOT"
  :description "A projects that solves the tasks of the Yuppiechef Lead Developer assessment."
  :url "https://github.com/heinvde/yuppie-assessment"
  :dependencies [[org.clojure/clojure "1.12.0"]
                 [compojure "1.6.1"]
                 [ring/ring-defaults "0.3.2"]
                 [org.clojure/data.json "2.5.1"]
                 [clj-http "3.13.0"]
                 [mount "0.1.21"]
                 [environ "1.2.0"]]
  :plugins [[lein-ring "0.12.5"]
            [lein-environ "1.2.0"]]
  :repl-options {:init-ns yuppie-assessment.app}
  :ring {:handler yuppie-assessment.app/app
         :init yuppie-assessment.app/init-app
         :port 8080
         :auto-reload? true}
  :profiles
  {:dev [:project/dev :profiles/dev]
   :test [:project/test]
   :project/dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                                [ring/ring-mock "0.3.2"]]
                 :env {:google-oauth2-redirect-uri "http://localhost:8080/auth/verified"
                       :mysql-users-host "127.0.0.1"
                       :mysql-users-port 3306
                       :mysql-users-user "root"
                       :mysql-users-password "root"
                       :mysql-users-db-name "users"}}
   :project/test {:dependencies [[javax.servlet/servlet-api "2.5"]
                                 [ring/ring-mock "0.3.2"]]
                  :env {:google-client-id "fake-google-client-id"
                        :google-client-secret "fake-google-client-secret"
                        :google-oauth2-state-key "fake-state-key"
                        :google-oauth2-redirect-uri "http://localhost:8080/auth/verified"
                        :mysql-users-host "127.0.0.1"
                        :mysql-users-port 3306
                        :mysql-users-user "root"
                        :mysql-users-password "root"
                        :mysql-users-db-name "users"}}})
