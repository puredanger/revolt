(ns revolt.tasks.cljs
  (:require [clojure.tools.logging :as log]
            [cljs.build.api]
            [revolt.utils :as utils]))


(defn invoke
  [{:keys [optimizations builds]} classpaths target]
  (let [advanced? (or (= optimizations :advanced))])

  (run!
   (fn [build]
     (utils/timed (str "CLJS (" (:id build) ")")
      (cljs.build.api/build (:source-paths build)
                            (:compiler build))))
   (eduction
    (map #(-> %
              (update-in [:compiler :output-dir] (partial utils/ensure-relative-path target))
              (update-in [:compiler :output-to] (partial utils/ensure-relative-path target))
              (update-in [:compiler :optimizations] (fn [current given] (or given current :none)) optimizations)
              (as-> conf
                  (utils/dissoc-maybe conf [:compiler :preloads] (= (-> conf :compiler :optimizations) :advanced)))))
    builds)))
