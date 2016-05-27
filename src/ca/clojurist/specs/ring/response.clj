(ns ca.clojurist.specs.ring.response
  "Provides a spec for a Ring request."
  {:author "Robert Medeiros" :email "robert@clojurist.ca"}
  (:require
   [clojure.spec :as s]
   [clojure.string :as string]))

;; Comments before each attribute are taken from the Ring SPEC:
;;
;;   https://github.com/ring-clojure/ring/blob/master/SPEC


;; Attributes
;; -------------------------------------------------------------------

;;
;; REQUIRED
;;

;; :status
;;   (Required, Integer)
;;   The HTTP status code, must be greater than or equal to 100.

(s/def ::status
  (s/and integer? #(>= % 100)))

;; :headers
;;   (Required, IPersistentMap)
;;   A Clojure map of HTTP header names to header values. These values may be
;;   either Strings, in which case one name/value header will be sent in the
;;   HTTP response, or a seq of Strings, in which case a name/value header will be
;;   sent for each such String value.

(s/def ::headers
  (s/map-of string? (s/or string?
                          (s/cat :strings string?))))

;; :body
;;   (Optional, {String, ISeq, File, InputStream})
;;   A representation of the response body, if a response body is appropriate for
;;   the response's status code. The respond body is handled according to its type:
;;   String:
;;     Contents are sent to the client as-is.
;;   ISeq:
;;     Each element of the seq is sent to the client as a string.
;;   File:
;;     Contents at the specified location are sent to the client. The server may
;;     use an optimized method to send the file if such a method is available.
;;   InputStream:
;;     Contents are consumed from the stream and sent to the client. When the
;;     stream is exhausted, it is .close'd.

(s/def ::body
  (s/or
   string?
   seq?
   #(instance? % java.util.File)
   #(instance? java.io.InputStream)))

;; Ring response
;; -------------------------------------------------------------------
;; A response map is a Clojure map containing at least the following
;; keys and corresponding values:

(s/def ::ring-response
  (s/keys
   :req [::status
         ::headers]
   :opt [::body]))
