(defproject yuppie-assessment "0.1.0-SNAPSHOT"
  :description "A projects that solves the tasks of the Yuppiechef Lead Developer assessment."
  :url "https://github.com/heinvde/yuppie-assessment"
  :dependencies [[org.clojure/clojure "1.12.0"]
                 [compojure "1.6.1"]
                 [ring/ring-defaults "0.3.2"]
                 [org.clojure/data.json "2.5.1"]]
  :plugins [[lein-ring "0.12.5"]]
  :repl-options {:init-ns yuppie-assessment.app}
  :ring {:handler yuppie-assessment.app/app
         :port 8080
         :auto-reload? true}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.2"]]}})
