(ns yuppie-assessment.users.rabbitmq.consumers-test
  (:require [clojure.test :refer :all]
            [clojure.data.json :as json]
            [yuppie-assessment.users.rabbitmq.consumers :as consumers]
            [yuppie-assessment.cloudinary.client :as cloudinary]
            [yuppie-assessment.users.repository.mysql :as mysql-repo]))

(deftest test-upload-profile-picture
  (testing "can upload profile picture"
    (let [id "my-id"
          picture-url "https://my.com/pic"
          profile {:id id
                   :first-name "my-first-name"
                   :last-name "my-last-name"
                   :profile-picture-url picture-url}]
      (with-redefs [cloudinary/upload-image-from-url
                    (fn [_ url]
                      (is (= picture-url url))
                      {:url "https://my.fancy.com/pic"})
                    mysql-repo/update-user-profile-by-id
                    (fn [_ id profile]
                      (is (= "my-id" id))
                      (is (= {:profile-picture-url "https://my.fancy.com/pic"}
                             profile)))]
        (#'consumers/update-with-cloudinary-picture nil
                                                    nil
                                                    (.getBytes (json/write-str profile)))))))
