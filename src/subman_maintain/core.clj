(ns subman-maintain.core
  (:require [clojurewerkz.elastisch.rest.document :as esd]
            [clojurewerkz.elastisch.rest :as esr]
            [clojurewerkz.elastisch.rest.index :as esi]
            [monger.collection :as mc]
            [monger.core :as mg]
            [environ.core :refer [env]]))

(def raw-db-conn (mg/connect {:host (env :raw-db-host)
                              :port (-> :raw-db-port env Integer.)}))

(def raw-db (mg/get-db raw-db-conn (env :raw-db-name)))

(def db-conn (esr/connect (env :db-host)))

; Indexes:

(defn create-index!
  []
  (esi/create (get-dep :db-connection)
              (env :index-name)
              :mappings {"subtitle"
                         {:properties {:show {:type "string"}
                                       :season {:type "string"
                                                :index "not_analyzed"}
                                       :episode {:type "string"
                                                 :index "not_analyzed"}
                                       :name {:type "string"}
                                       :lang {:type "string"}
                                       :version {:type "string"}
                                       :url {:type "string"
                                             :index "not_analyzed"}
                                       :source {:type "integer"}}}}))

(defn create-raw-index!
  []
  (mc/ensure-index raw-db "subtitle" (array-map :url 1) {:uniquer true}))

; Migrations:

(defn from-index-to-raw-db!
  []
  (let [total-count (-> (esd/search db-conn
                                    (env :index-name) "subtitle")
                        :hits
                        :total)
        limit 1000]
    (loop [offset 0]
      (let [subtitles (esd/search db-conn
                                  (env :index-name) "subtitle"
                                  :from offset
                                  :size limit)]
        (doseq [item (->> subtitles :hits :hits (map :_source))]
          (mc/insert raw-db "subtitle" item))
        (when-not (> (+ offset limit) total-count)
          (println "Moved:" offset)
          (recur (+ offset limit)))))))
