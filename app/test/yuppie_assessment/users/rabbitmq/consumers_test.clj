(ns yuppie-assessment.users.rabbitmq.consumers-test
  (:require [clojure.test :refer :all]
            [clojure.data.json :as json]
            [yuppie-assessment.cloudinary.client :as cloudinary]
            [yuppie-assessment.users.rabbitmq.consumers :as consumers]
            [yuppie-assessment.users.repository.mysql :as mysql-repo]))

(deftest test-upload-profile-picture
  (testing "can upload profile picture"
    (let [id "my-id"
          profile {:id id
                   :first-name "my-first-name"
                   :last-name "my-last-name"
                   :profile-picture-url "https://my.com/pic"}]
      (with-redefs [cloudinary/upload-image-from-url
                    (fn [_ url]
                      (is (= "https://my.com/pic" url))
                      {:url "https://my.fancy.com/pic"})
                    mysql-repo/update-user-profile-by-id
                    (fn [_ id profile]
                      (is (= "my-id" id))
                      (is (= {:profile-picture-url "https://my.fancy.com/pic"}
                             profile)))]
        (consumers/upload-profile-picture nil
                                          nil
                                          (.getBytes (json/write-str profile))))))
  (testing "can upload profile picture"
    (let [id "my-id"
          profile {:id id
                   :first-name "my-first-name"
                   :last-name "my-last-name"
                   :profile-picture-url "https://res.cloudinary.com/pic"}]
      (with-redefs [cloudinary/upload-image-from-url
                    (fn [_ _]
                      (is false "Should not call upload image"))
                    mysql-repo/update-user-profile-by-id
                    (fn [_ _ _]
                      (is false "Should not call update user profile"))]
        (consumers/upload-profile-picture nil
                                          nil
                                          (.getBytes (json/write-str profile)))))))
