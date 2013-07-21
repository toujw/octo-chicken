(ns octo-chicken.data.list-zipper
  (:refer-clojure :exclude [replace]))

(defrecord Zipper [lst crumbs])

(defn zipper?
  "Returns true if the arg is a Zipper"
  [zipper]
  (instance? Zipper zipper))

(defn- zipper-or-nil?
  "Returns true if the arg is a Zipper or nil"
  [zipper]
  (or (zipper? zipper) (nil? zipper)))

(defn make-zipper
  "Makes a list zipper"
  ([]
     (make-zipper '()))
  ([coll]
     (->Zipper (list* coll) '())))

(defn right
  "Moves the zipper to the right or returns nil if at the last element"
  [zipper]
  {:pre [(zipper-or-nil? zipper)]}
  (when (and zipper (> (count (.lst zipper)) 1))
    (->Zipper (rest (.lst zipper)) (cons (first (.lst zipper)) (.crumbs zipper)))))

(defn left
  "Moves the zipper to the left or returns nil if at the head"
  [zipper]
  {:pre [(zipper-or-nil? zipper)]}
  (when (and zipper (> (count (.crumbs zipper)) 0))
    (->Zipper (cons (first (.crumbs zipper)) (.lst zipper)) (rest (.crumbs zipper)))))

(defn replace
  "Replaces the item at this location"
  [zipper item]
  {:pre [(zipper-or-nil? zipper)]}
  (when zipper
    (->Zipper (cons item (rest (.lst zipper))) (.crumbs zipper))))

(defn edit
  "Replaces the item at this location with (f item args)"
  [zipper f & args]
  {:pre [(zipper-or-nil? zipper)]}
  (when zipper
    (replace zipper (apply f (first (.lst zipper)) args))))

(defn insert-right
  "Inserts an item to the right of the current location"
  [zipper item]
  {:pre [(zipper-or-nil? zipper)]}
  (when zipper
    (->Zipper (cons (first (.lst zipper)) (cons item (rest (.lst zipper)))) (.crumbs zipper))))

(defn insert-left
  "Inserts an item to the left of the current location"
  [zipper item]
  {:pre [(zipper-or-nil? zipper)]}
  (when zipper
    (->Zipper (.lst zipper) (cons item (.crumbs zipper)))))

(defn head
  "Zips to the far left and returns the entire list"
  [zipper]
  {:pre [(zipper-or-nil? zipper)]}
  (when zipper
    (concat (reverse (.crumbs zipper)) (.lst zipper))))
