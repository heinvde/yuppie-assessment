(ns yuppie-assessment.logger)

(defn- log [severity & messages]
  (apply println (str severity "::" (java.time.LocalDateTime/now)) messages))

(defn log-info
  "Logs a message to the console."
  [& messages]
  (apply log "INFO" messages))

(defn log-error
  "Logs an error message to the console."
  [& messages]
  (apply log "ERROR" messages))

(defn log-warning
  "Logs a warning message to the console."
  [& messages]
  (apply log "WARNING" messages))
