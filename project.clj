(defproject subman-maintain "0.1.0-SNAPSHOT"
            :description "Migration scripts and REPL"
            :url "https://github.com/submanio/subman-maintain"
            :license {:name "Eclipse Public License"
                      :url "http://www.eclipse.org/legal/epl-v10.html"}
            :dependencies [[org.clojure/clojure "1.6.0"]
                           [environ "1.0.0"]
                           [clojurewerkz/elastisch "2.1.0"]
                           [com.novemberain/monger "2.0.1"]]
            :repl-options {:init-ns subman-maintain.core}
            :plugins [[lein-environ "1.0.0"]]
            :env {:db-host "http://127.0.0.1:9200"
                  :index-name "subman7"
                  :raw-db-host "localhost"
                  :raw-db-port "27017"
                  :raw-db-name "subman7"})
