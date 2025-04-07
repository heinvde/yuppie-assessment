(ns yuppie-assessment.cloudinary.client-test
  (:require [clojure.test :refer :all]
            [yuppie-assessment.cloudinary.client :as client]
            [yuppie-assessment.config :refer [config]]
            [mount.core :as mount]))


(use-fixtures :once
  (fn [run-tests]
    (mount/start)
    (run-tests)
    (mount/stop)))

(deftest ^:integration upload-image-from-url
  (testing "can upload file to cloudinary"
    (client/upload-image-from-url (config :cloudinary)
                                  "https://cloudinary-devs.github.io/cld-docs-assets/assets/images/coffee_cup.jpg"
                                  {"overwrite" true
                                   "unique_filename" false
                                   "use_filename" true})
    (Thread/sleep 10)
    (is true)))