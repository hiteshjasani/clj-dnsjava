# clj-dnsjava

A Clojure library that wraps dnsjava with a better api.

[![Clojars Project](https://img.shields.io/clojars/v/org.jasani/clj-dnsjava.svg)](https://clojars.org/org.jasani/clj-dnsjava)


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
(clojure.pprint/print-table (dns/ns-lookup :mx "google.com"))
=>
| :type |       :name | :ttl |               :addl-name | :priority |                  :target |
|-------+-------------+------+--------------------------+-----------+--------------------------|
|   :mx | google.com. |  492 | alt2.aspmx.l.google.com. |        30 | alt2.aspmx.l.google.com. |
|   :mx | google.com. |  492 |      aspmx.l.google.com. |        10 |      aspmx.l.google.com. |
|   :mx | google.com. |  492 | alt3.aspmx.l.google.com. |        40 | alt3.aspmx.l.google.com. |
|   :mx | google.com. |  492 | alt4.aspmx.l.google.com. |        50 | alt4.aspmx.l.google.com. |
|   :mx | google.com. |  492 | alt1.aspmx.l.google.com. |        20 | alt1.aspmx.l.google.com. |

```

## License

Copyright © 2017 Hitesh Jasani

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
