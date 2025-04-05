(ns yuppie-assessment.assert-helpers
  (:require [clojure.test :refer :all]))

(defn contains-many? [& keys]
  (fn [arg]
    (every? #(contains? arg %) keys)))

(defn test-mock
  ([name expected-args result]
   (fn [& args]
     (is (= (count expected-args) (count args)) (str "Unexpected number of arguments for " name))
     (doseq [[check arg] (map #(vec [%1 %2]) expected-args args)]
       (is (check arg) (str "Failed check for " name " on '" arg "' arg using check fn: " check)))
     result))
  ([result] (fn [] result)))
