(ns yuppie-assessment.users.model.profile)

(defn profile->mysql-profile
  "Transforms profile map into mysql profile map"
  [{:keys [id first-name last-name email-address]}]
  {:pre [(string? id) first-name email-address]}
  (array-map :id id
             :first_name first-name
             :last_name (or last-name "")
             :email email-address))
