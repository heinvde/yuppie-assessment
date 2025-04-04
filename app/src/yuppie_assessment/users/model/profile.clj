(ns yuppie-assessment.users.model.profile)

(defn profile->mysql-profile
  "Transforms profile map into mysql profile map"
  [{:keys [id first-name last-name email-address]}]
  {:pre [(or (uuid? id) (string? id)) first-name email-address]}
  (array-map :id (str id)
             :first_name first-name
             :last_name (or last-name "")
             :email email-address))

(defn profile->mysql-update
  "Transforms profile map into mysql update map"
  [{:keys [first-name last-name email-address]}]
  (->> (array-map :first_name first-name
                  :last_name last-name
                  :email email-address)
       (filter #(not (nil? (second %))))
       (into {})))

(defn mysql-profile->profile
  "Transforms mysql profile map into profile map"
  [{:keys [id first_name last_name email]}]
  {:pre [(or (uuid? id) (string? id)) first_name email]}
  (array-map :id (if (string? id) (java.util.UUID/fromString id) id)
             :first-name first_name
             :last-name last_name
             :email-address email))