# clj-dnsjava

A Clojure library that wraps dnsjava with a better api.

## Usage


```
(:require [clj-dnsjava.core :as dns])

;; defaults to looking up A records
(dns/ns-lookup "google.com")
=> [{:type :a, :name "google.com.", :ttl 249, :address "216.58.217.110"}]

;; record keywords are found in clj-dnsjava.core/keyword-type
(dns/ns-lookup "google.com" :aaaa)
=> [{:type :aaaa, :name "google.com.", :ttl 298, :address "2607:f8b0:4004:80c:0:0:0:200e"}]

;; supports different order for args
(dns/ns-lookup :mx "google.com")
=> [{:type :mx, :name "google.com.", :ttl 600, :addl-name "alt3.aspmx.l.google.com.", :priority 40, :target "alt3.aspmx.l.google.com."} {:type :mx, :name "google.com.", :ttl 600, :addl-name "alt1.aspmx.l.google.com.", :priority 20, :target "alt1.aspmx.l.google.com."} {:type :mx, :name "google.com.", :ttl 600, :addl-name "aspmx.l.google.com.", :priority 10, :target "aspmx.l.google.com."} {:type :mx, :name "google.com.", :ttl 600, :addl-name "alt2.aspmx.l.google.com.", :priority 30, :target "alt2.aspmx.l.google.com."} {:type :mx, :name "google.com.", :ttl 600, :addl-name "alt4.aspmx.l.google.com.", :priority 50, :target "alt4.aspmx.l.google.com."}]


```

## License

Copyright © 2017 Hitesh Jasani

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
