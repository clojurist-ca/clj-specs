(ns ca.clojurist.specs.ring.request
  "Provides a spec for a Ring request."
  {:author "Robert Medeiros" :email "robert@clojurist.ca"}
  (:require
   [clojure.spec :as s]
   [clojure.string :as string]))

;; Comments before each attribute are taken from the Ring SPEC:
;;
;;   https://github.com/ring-clojure/ring/blob/master/SPEC

;; Note that the following attributes have been deprecated:
;;
;; * :content-type - The MIME type of the request body, if known.
;; * :content-length - The number of bytes in the request body, if
;;   known.
;; * :character-encoding - The name of the character encoding used in
;;   the request body, if known.


;; This regex matches 0-255 (part of a dotted quad representation of
;; an IPv4 address):

(def ipv4-regex #"^([01]?\d\d?|2[0-4]\d|25[0-5])\.([01]?\d\d?|2[0-4]\d|25[0-5])\.([01]?\d\d?|2[0-4]\d|25[0-5])\.([01]?\d\d?|2[0-4]\d|25[0-5])$")

;; TODO
(def ipv6-regex #"FIXME")

;; Attributes
;; -------------------------------------------------------------------

;; A schema for an IPv4 address in dotted quad notation.
(s/def ::ipv4
  (s/and string? #(re-matches ipv4-regex %)))

;; A schema for an IPv6 address.
(s/def ::ipv6
  (s/and string? #(re-matches ipv6-regex)))

(s/def ::inet-address
  (s/or
   :ipv4 ::ipv4
   :ipv6 ::ipv6))

;;
;; REQUIRED
;;

;; :scheme
;;   (Required, Keyword)
;;   The transport protocol, must be one of :http or :https.

(s/def ::scheme #{:http :https})

;; :protocol
;;   (Required, String)
;;   The protocol the request was made with, e.g. "HTTP/1.1".

(s/def ::protocol string?)

;; :server-port
;;   (Required, Integer)
;;   The port on which the request is being handled.
;;
;; (NB: port 0 is reserved and unavailable.)

(s/def ::server-port
  (s/and integer?
         #(<= 1 % 65535)))

;; :server-name
;;   (Required, String)
;;   The resolved server name, or the server IP address.

(s/def ::server-name string?)

;; :remote-addr
;;   (Required, String)
;;   The IP address of the client or the last proxy that sent the request.

(s/def ::remote-addr ::inet-address)

;; :request-method
;;   (Required, Keyword)
;;   The HTTP request method, must be a lowercase keyword corresponding to a HTTP
;;   request method, such as :get or :post.

(s/def ::request-method
  #{:get
    :head
    :options
    :put
    :post
    :delete
    :trace})

;; :headers
;;   (Required, IPersistentMap)
;;   A Clojure map of downcased header name Strings to corresponding header value
;;   Strings.

(s/def ::headers (s/map-of string? string?))

;; :uri
;;  (Required, String)
;;  The request URI, excluding the query string and the "?" separator.
;;  Must start with "/".

(s/def ::uri
  (s/and string?
         #(string/starts-with? % "/")))

;;
;; OPTIONAL
;;

;; :body
;;   (Optional, InputStream)
;;   An InputStream for the request body, if present.

(s/def ::body #(instance? java.io.InputStream %))

;; :query-string
;;   (Optional, String)
;;   The query string, if present.

(s/def ::query-string string?)

;; :ssl-client-cert
;;   (Optional, X509Certificate)
;;   The SSL client certificate, if supplied.

(s/def ::ssl-client-cert #(instance? % java.security.cert.X509Certificate))


;; Ring request
;; -------------------------------------------------------------------
;; A request map is a Clojure map containing at least the following keys
;; and corresponding values:

(s/def ::ring-request
  (s/keys
   :req [::scheme
         ::protocol
         ::server-port
         ::server-name
         ::remote-addr
         ::request-method
         ::headers
         ::uri]
   :opt [::body
         ::query-string
         ::ssl-client-cert]))
