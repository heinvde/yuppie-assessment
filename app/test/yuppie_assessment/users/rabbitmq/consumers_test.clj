(ns yuppie-assessment.users.rabbitmq.consumers-test
  (:require [clojure.test :refer :all]
            [clojure.data.json :as json]
            [yuppie-assessment.users.rabbitmq.consumers :as consumers]
            [yuppie-assessment.users.updates :as user-updates]
            [yuppie-assessment.users.repository.mysql :as mysql-repo]))

(deftest test-upload-profile-picture
  (testing "can upload profile picture"
    (let [id "my-id"
          profile {:id id
                   :first-name "my-first-name"
                   :last-name "my-last-name"
                   :profile-picture-url "https://my.com/pic"}]
      (with-redefs [user-updates/upload-profile-picture-to-cloudinary
                    (fn [id]
                      (is (= "my-id" id))
                      {:url "https://my.fancy.com/pic"})
                    mysql-repo/update-user-profile-by-id
                    (fn [_ id profile]
                      (is (= "my-id" id))
                      (is (= {:profile-picture-url "https://my.fancy.com/pic"}
                             profile)))]
        (#'consumers/update-with-cloudinary-picture nil
                                                    nil
                                                    (.getBytes (json/write-str profile)))))))
