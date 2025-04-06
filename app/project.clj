(defproject yuppie-assessment "0.1.0-SNAPSHOT"
  :description "A projects that solves the tasks of the Yuppiechef Lead Developer assessment."
  :url "https://github.com/heinvde/yuppie-assessment"
  :dependencies [[org.clojure/clojure "1.12.0"]
                 [compojure "1.6.1"]
                 [ring/ring-defaults "0.3.2"]
                 [org.clojure/data.json "2.5.1"]
                 [clj-http "3.13.0"]
                 [org.clojure/java.jdbc "0.7.12"]
                 [mysql/mysql-connector-java "5.1.38"]
                 [com.mchange/c3p0 "0.9.5.2"]
                 [mount "0.1.21"]
                 [com.novemberain/langohr "5.5.0"]
                 [environ "1.2.0"]]
  :plugins [[lein-ring "0.12.5"]
            [lein-environ "1.2.0"]]
  :repl-options {:init-ns yuppie-assessment.app}
  :ring {:handler yuppie-assessment.app/app
         :init yuppie-assessment.app/init-app
         :destroy yuppie-assessment.app/shutdown-app
         :port 8080
         :auto-reload? true}
  :test-selectors {:default (complement :integration)
                   :integration :integration
                   :all (fn [_] true)}
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
                       :mysql-users-db-name "users"
                       :rabbitmq-default-host "localhost"
                       :rabbitmq-default-port 5672
                       :rabbitmq-default-username "guest"
                       :rabbitmq-default-password "guest"}}
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
                        :mysql-users-db-name "users"
                        :rabbitmq-default-host "localhost"
                        :rabbitmq-default-port 5672
                        :rabbitmq-default-username "guest"
                        :rabbitmq-default-password "guest"}}})
