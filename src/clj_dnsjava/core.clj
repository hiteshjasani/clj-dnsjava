(ns clj-dnsjava.core
  (:require [clojure.pprint :as pp])
  (:import
   (java.net InetAddress Inet4Address Inet6Address)
   (org.xbill.DNS Type Lookup
                  Record ARecord AAAARecord CNAMERecord DSRecord MXRecord NSRecord PTRRecord SOARecord TXTRecord TXTBase)
   ))

(def keyword-type
  [[:a Type/A]
   [:a6 Type/A6]
   [:aaaa Type/AAAA]
   [:afsdb Type/AFSDB]
   [:any Type/ANY]
   [:apl Type/APL]
   [:atma Type/ATMA]
   [:axfr Type/AXFR]
   [:cert Type/CERT]
   [:cname Type/CNAME]
   [:dhcid Type/DHCID]
   [:dlv Type/DLV]
   [:dname Type/DNAME]
   [:dnskey Type/DNSKEY]
   [:ds Type/DS]
   [:eid Type/EID]
   [:gpos Type/GPOS]
   [:hinfo Type/HINFO]
   [:ipseckey Type/IPSECKEY]
   [:isdn Type/ISDN]
   [:ixfr Type/IXFR]
   [:key Type/KEY]
   [:kx Type/KX]
   [:loc Type/LOC]
   [:maila Type/MAILA]
   [:mailb Type/MAILB]
   [:mb Type/MB]
   [:md Type/MD]
   [:mf Type/MF]
   [:mg Type/MG]
   [:minfo Type/MINFO]
   [:mr Type/MR]
   [:mx Type/MX]
   [:naptr Type/NAPTR]
   [:nimloc Type/NIMLOC]
   [:ns Type/NS]
   [:nsap Type/NSAP]
   [:nsap-ptr Type/NSAP_PTR]
   [:nsec Type/NSEC]
   [:nsec3 Type/NSEC3]
   [:nsec3param Type/NSEC3PARAM]
   [:null Type/NULL]
   [:nxt Type/NXT]
   [:opt Type/OPT]
   [:ptr Type/PTR]
   [:px Type/PX]
   [:rp Type/RP]
   [:rrsig Type/RRSIG]
   [:rt Type/RT]
   [:sig Type/SIG]
   [:soa Type/SOA]
   [:spf Type/SPF]
   [:srv Type/SRV]
   [:sshfp Type/SSHFP]
   [:tkey Type/TKEY]
   [:tlsa Type/TLSA]
   [:tsig Type/TSIG]
   [:txt Type/TXT]
   [:uri Type/URI]
   [:wks Type/WKS]
   [:x25 Type/X25]
   ])

(def keyword-type-table (into {} keyword-type))
(def type-keyword-table (into {} (mapv (fn [[a b]] [b a]) keyword-type)))
(defn get-lookup-type
  [^clojure.lang.Keyword kw]
  (get keyword-type-table kw))
(defn get-kw-from-lookup-type
  [^Integer look-type]
  (get type-keyword-table look-type))

(defn invoke-protected-method
  "Hack to work around https://dev.clojure.org/jira/browse/CLJ-1243"
  ([java-obj meth-name]
   (let [m (.getDeclaredMethod (class java-obj) meth-name (into-array Class []))]
     (.setAccessible m true)
     (.invoke m java-obj nil)))

  ([klass java-obj meth-name]
   (let [m (.getDeclaredMethod klass meth-name (into-array Class []))]
     (.setAccessible m true)
     (.invoke m java-obj nil))))

(defn convert-rec
  [rec]
  (let [rec-type (get-kw-from-lookup-type (.getType rec))
        address  ()]
    {:type rec-type #_:class #_ (class rec) :name (.toString (.getName rec)) :ttl (.getTTL rec)}))

(defprotocol ConvertibleToKWHashProtocol
  "Convertible to a keyword hash"
  (convert [a] "Convert data type to a keyword hash"))

(extend-type ARecord
  ConvertibleToKWHashProtocol
  (convert [o] (assoc (convert-rec o)
                      :address (.getHostAddress (.getAddress o)))))

(extend-type AAAARecord
  ConvertibleToKWHashProtocol
  (convert [o] (assoc (convert-rec o)
                      :address (.getHostAddress (.getAddress o)))))

(extend-type MXRecord
  ConvertibleToKWHashProtocol
  (convert [o] (assoc (convert-rec o)
                      :addl-name (.toString (.getAdditionalName o))
                      :priority (.getPriority o)
                      :target (.toString (.getTarget o)))))

(extend-type SOARecord
  ConvertibleToKWHashProtocol
  (convert [o] (assoc (convert-rec o)
                      :admin (.toString (.getAdmin o))
                      :expire (.getExpire o)
                      :host (.toString (.getHost o))
                      :minimum (.getMinimum o)
                      :refresh (.getRefresh o)
                      :retry (.getRetry o)
                      :serial (.getSerial o))))

(extend-type TXTRecord
  ConvertibleToKWHashProtocol
  (convert [o] (assoc (convert-rec o)
                      :strings (invoke-protected-method TXTBase o "getStrings"))))

(extend-type Record
  ConvertibleToKWHashProtocol
  (convert [o] (convert-rec o)))

(defn print-recs
  [recs]
  (doseq [rec recs]
    (println (str rec))
    (println (str (convert-rec rec)))))

(defn lookup-impl
  [look-type domain]
  (.run (Lookup. domain look-type)))

;;(ns-unmap *ns* 'ns-lookup)              ; Enable reload of multi def in REPL
(defmulti ns-lookup
  "Lookup DNS record for a domain.

   (ns-lookup \"google.com\")           ; defaults to :a record

   (ns-lookup \"google.com\" :a)
   (ns-lookup \"google.com\" :mx)

   (ns-lookup :a \"google.com\")
   (ns-lookup :mx \"google.com\")

   (ns-lookup \"google.com\" Type/A)
   (ns-lookup \"google.com\" Type/MX)

   (ns-lookup Type/A \"google.com\")
   (ns-lookup Type/MX \"google.com\")
  "
  (fn [x & xs]
    (mapv class (into [x] xs))))

(defmethod ns-lookup [String] [domain]
  (mapv convert (lookup-impl Type/A domain)))

(defmethod ns-lookup [Integer String] [look-type domain]
  (mapv convert (lookup-impl look-type domain)))

(defmethod ns-lookup [String Integer] [domain look-type]
  (mapv convert (lookup-impl look-type domain)))

(defmethod ns-lookup [String clojure.lang.Keyword] [domain kw-look-type]
  (mapv convert (lookup-impl (get-lookup-type kw-look-type) domain)))

(defmethod ns-lookup [clojure.lang.Keyword String] [kw-look-type domain]
  (mapv convert (lookup-impl (get-lookup-type kw-look-type) domain)))
