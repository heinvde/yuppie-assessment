(ns yuppie-assessment.users.updates-test
  (:require [clojure.test :refer :all]
            [mount.core :as mount]
            [yuppie-assessment.config]
            [yuppie-assessment.google.client :as google]
            [yuppie-assessment.users.repository.mysql :as mysql-repo]
            [yuppie-assessment.users.updates :as user-updates]))

(defn- arg-contains? [& keys]
  (fn [arg]
    (every? #(contains? arg %) keys)))

(defn- test-mock
  ([name expected-args result]
   (fn [& args]
     (is (= (count expected-args) (count args)) (str "Unexpected number of arguments for " name))
     (doseq [[check arg] (map #(vec [%1 %2]) expected-args args)]
       (is (check arg) (str "Failed check for " name " on '" arg "' arg using check fn: " check)))
     result))
  ([result] (fn [] result)))

(def check-profile
  (fn [expected profile]
    (and
     (or (uuid? (:id profile)) (string? (:id profile)))
     (= (:first-name expected) (:first-name profile))
     (= (:last-name expected) (:last-name profile))
     (= (:email-address expected) (:email-address profile)))))

(use-fixtures :once
  (fn [run-tests]
    (mount/start #'yuppie-assessment.config/config)
    (run-tests)
    (mount/stop)))

(deftest test-create-user-with-google-oauth
  (testing "can create user using google oauth code"
    (let [google-profile {:first-name "my-first-name"
                          :last-name "my-last-name"
                          :email-address "my-email"}]
      (with-redefs
       [google/oauth2-code->access-token (test-mock
                                          "oauth2-code->access-token"
                                          [(arg-contains? :client-id :client-secret)
                                           #(= % "my-oauth-code")
                                           string?]
                                          {:access-token "my-access-token"})
        google/get-user-profile (test-mock
                                 "get-user-profile"
                                 [#(string? (:access-token %))]
                                 google-profile)
        mysql-repo/get-user-profile-by-email (fn [_ email]
                                               (is (= "my-email" email))
                                               nil)
        mysql-repo/insert-user-profile (test-mock
                                        "insert-user-profile"
                                        [keyword? (partial check-profile google-profile)]
                                        nil)]
        (is (check-profile google-profile
                           (user-updates/create-user-with-google-oauth "my-oauth-code"))))))
  (testing "can update user using google oauth code when already exists"
    (let [google-profile {:first-name "my-first-name"
                          :last-name "my-last-name"
                          :email-address "my-email"}]
      (with-redefs
       [google/oauth2-code->access-token (test-mock
                                          "oauth2-code->access-token"
                                          [(arg-contains? :client-id :client-secret)
                                           #(= % "my-oauth-code")
                                           string?]
                                          {:access-token "my-access-token"})
        google/get-user-profile (test-mock
                                 "get-user-profile"
                                 [#(string? (:access-token %))]
                                 google-profile)
        mysql-repo/get-user-profile-by-email (fn [_ email]
                                               (is (= "my-email" email))
                                               {:id "i-exist"})
        mysql-repo/insert-user-profile nil
        mysql-repo/update-user-profile-by-email (test-mock
                                          "update-user-profile-by-email"
                                          [keyword?
                                           #(= % "my-email")
                                           (partial check-profile google-profile)]
                                          nil)]
        (is (check-profile google-profile
                           (user-updates/create-user-with-google-oauth "my-oauth-code")))))))
