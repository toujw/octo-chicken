(ns octo-chicken.io)

(defn input-stream->lazy-seq
  "Converts a java.io.InputStream to a clojure.lang.LazySeq."
  [^java.io.InputStream s]
  (when s
    (lazy-seq
     (let [buffer (byte-array 8192)
           rd (.read s buffer)]
       (when-not (= rd -1)
         (concat (take rd buffer) (input-stream->lazy-seq s)))))))
