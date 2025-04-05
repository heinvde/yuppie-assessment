(ns yuppie-assessment.users.model.profile)

(defn profile->mysql-profile
  "Transforms profile map into mysql profile map"
  [{:keys [id first-name last-name email-address profile-picture-url]}]
  {:pre [(or (uuid? id) (string? id)) first-name email-address]}
  (array-map :id (str id)
             :first_name first-name
             :last_name (or last-name "")
             :email email-address
             :profile_picture_url (or profile-picture-url nil)))

(defn profile->mysql-update
  "Transforms profile map into mysql update map"
  [{:keys [first-name last-name email-address profile-picture-url]}]
  (->> (array-map :first_name first-name
                  :last_name last-name
                  :email email-address
                  :profile_picture_url profile-picture-url)
       (filter #(not (nil? (second %)))) ; avoid overriding missing values on updates
       (into {})))

(defn mysql-profile->profile
  "Transforms mysql profile map into profile map"
  [{:keys [id first_name last_name email profile_picture_url]}]
  {:pre [(or (uuid? id) (string? id)) first_name email]}
  (array-map :id (if (string? id) (java.util.UUID/fromString id) id)
             :first-name first_name
             :last-name last_name
             :email-address email
             :profile-picture-url profile_picture_url))