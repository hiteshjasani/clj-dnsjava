(ns clj-dnsjava.core-test
  (:require [clojure.test :refer :all]
            [clj-dnsjava.core :refer :all])
  (:import (org.xbill.DNS Type))
  )

(defn match-return-types
  [recs match-type]
  (and (seq recs)
       (every? #(= match-type (:type %)) recs)))

(deftest flexible-arg-order
  (testing "domain"
    (is (match-return-types (ns-lookup "google.com") :a))
    (is (not (match-return-types (ns-lookup "google.com") :aaaa))))

  (testing "lookup type and domain"
    (is (match-return-types (ns-lookup Type/A "google.com") :a))
    (is (match-return-types (ns-lookup Type/MX "google.com") :mx))
    (is (match-return-types (ns-lookup "google.com" Type/A) :a))
    (is (match-return-types (ns-lookup "google.com" Type/MX) :mx))
    )

  (testing "keyword type and domain"
    (is (match-return-types (ns-lookup :a "google.com") :a))
    (is (match-return-types (ns-lookup :mx "google.com") :mx))
    (is (match-return-types (ns-lookup "google.com" :a) :a))
    (is (match-return-types (ns-lookup "google.com" :mx) :mx))
    )
  )

(deftest text-records
  (testing "google"
    (is (match-return-types (ns-lookup :txt "google.com") :txt))
    ))

(deftest cname-records
  (testing "amazon"
    (is (match-return-types (ns-lookup "www.amazon.com" :cname) :cname))
    ))

(deftest reverse-lookup
  (testing "google"
    (is (= "google-public-dns-a.google.com." (rev-lookup "8.8.8.8")))
    ))
