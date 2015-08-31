(ns property-test.core-test
  (:require [clojure.test :refer :all]
            [clj-time.core :as t]
            [clj-time.format :as f]
            [property-test.core :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]))

(def batch-param-gen
  (gen/fmap #(str (first %) "=" (second %))
            (gen/tuple (gen/elements ["offset" "limit"]) gen/pos-int)))

(def broadcast-fields-gen
  (gen/elements ["end" "id" "imi" "recordLink" "selfLink"
                 "start" "statistics" "twitterInfo"]))

(def channel-fields-gen
  (gen/elements ["broadcastsLink" "entitlementCodes" "genres"
                 "horizonLink" "logicalPosition" "logoLink"
                 "name" "ref" "selfLink" "synopsis" "tvaServiceIdRef"]))

(defn embedded-fields [embedded field-generator]
  (gen/fmap #(str embedded "." %) field-generator))

(def broadcast-field-query-gen
  (let [channel-embedded-fields-gen (embedded-fields "channel" channel-fields-gen)]
    (gen/fmap #(str "field=" (clojure.string/join "," %))
              (gen/vector (gen/one-of [broadcast-fields-gen
                                       channel-embedded-fields-gen])
                          1
                          10))))

(def time-gen
  (gen/fmap #(f/unparse (f/formatters :date-time-no-ms) (t/plus (t/now) (t/hours %)))
            (gen/choose 0 6)))

(def time-range-gen
  (gen/fmap #(clojure.string/join "" %)
            (gen/tuple (gen/elements ["start" "end"])
                       (gen/elements ["=" "<" ">" "<=" ">="])
                       time-gen)))

(defn sort-gen [& field-gens]
  (gen/fmap #(str "sort=" %)
            (gen/one-of field-gens)))

(def broadcast-sort-gen (sort-gen
                         broadcast-fields-gen (embedded-fields "channel" channel-fields)))

(def full-broadcast-query-gen
  (gen/fmap #(clojure.string/join "&" %)
   (gen/vector (gen/one-of [batch-param-gen
                            broadcast-field-query-gen
                            time-range-gen
                            broadcast-sort-gen]))))

;; (gen/sample full-broadcast-query-gen) => ("" "" "start>2015-08-31T11:40:29Z" "end=2015-08-31T14:40:29Z&field=recordLink&sort=imi" "end>=2015-08-31T10:40:29Z&sort=imi" "end=2015-08-31T11:40:29Z&limit=3" "" "offset=5" "limit=0&field=channel.genres,recordLink,channel.genres,id,channel.synopsis,statistics&field=twitterInfo,channel.synopsis,imi&field=channel.selfLink,statistics,channel.name,imi,id&end<2015-08-31T12:40:29Z&field=id,id&field=channel.synopsis" "sort=id&sort=channel.genres&start<=2015-08-31T13:40:29Z&field=channel.selfLink,channel.logoLink&limit=8")
