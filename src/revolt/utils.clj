(ns revolt.utils
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
  (:import  (java.nio.file Paths)))

(defn gather-paths
  [paths]
  (into #{}
        (map #(-> (io/file %)
                  .toPath
                  .toAbsolutePath)
             paths)))

(defn ensure-ns
  "Ensures that a string, to be later transformed to a namespaced keyword,
  contains a namespace part. In case there is no namespace - a default one
  is added."
  [default-ns s]
  (if (second (and s (.split s "/")))
    s
    (str default-ns "/" s)))

(defn ensure-relative-path
  [target path]
  (when path
    (.toString (Paths/get target (.split path "/")))))

(defn build-params-list
  [input-params kw default-ns]
  (->> (str/split (kw input-params) #",")
       (map (partial ensure-ns default-ns))))

(defmacro timed
  "Evaluates expr and prints the time it took.  Returns the value of expr."
  [expr]
  `(let [start# (. System (nanoTime))
         ret# ~expr]
     (println (format "Elapsed time: %.2f secs" (/ (double (- (. System (nanoTime)) start#)) 1000000000.0)))
     ret#))