(ns octo-chicken.data.list-zipper-test
  (:refer-clojure :exclude [replace])
  (:require [octo-chicken.data.list-zipper :refer :all]
            [midje.sweet :refer :all]))

(def ^:private zipper (make-zipper (range 10)))

(defn- rights
  "Returns a function that will call right n times"
  [n]
  (apply comp (repeat n right)))

(defn- lefts
  "Returns a function that will call left n times"
  [n]
  (apply comp (repeat n left)))

(fact "make-zipper"
      (fact "Make a zipper from a list"
            zipper => (->Zipper (range 10) '()))
      (fact "Make a zipper from a vector"
            (make-zipper (vec (range 10))) => zipper))

(fact "right"
      (fact "Move to middle"
            ((rights 5) zipper) => (->Zipper (seq (range 5 10)) (reverse (range 5))))
      (fact "Move from beginning to end"
            ((rights 9) zipper) => (->Zipper '(9) (reverse (range 9))))
      (fact "Moving off the end of the list"
            ((rights 10) zipper) => nil))

(fact "left"
      (fact "Moving from middle towards beginning"
            ((comp (lefts 2) (rights 5)) zipper) => (->Zipper (seq (range 3 10)) (reverse (range 3))))
      (fact "Moving from 2nd position"
            ((comp (lefts 1) (rights 1)) zipper) => (->Zipper (seq (range 10)) '()))
      (fact "Moving off the end of the list"
            ((comp (lefts 6) (rights 5)) zipper) => nil))

(fact "replace"
      (fact "At beginning"
            (replace zipper :a) => (->Zipper (cons :a (range 1 10)) '()))
      (fact "In middle"
            (as-> zipper z ((rights 5) z) (replace z :a)) =>
            (->Zipper (cons :a (range 6 10)) (reverse (range 5))))
      (fact "At end"
            (as-> zipper z ((rights 9) z) (replace z :a)) => (->Zipper '(:a) (reverse (range 9)))))

(fact "edit"
      (fact "At beginning"
            (edit zipper + 10) => (->Zipper (cons 10 (range 1 10)) '()))
      (fact "In middle"
            (as-> zipper z ((rights 5) z) (edit z + 10)) =>
            (->Zipper (cons 15 (range 6 10)) (reverse (range 5))))
      (fact "At end"
            (as-> zipper z ((rights 9) z) (edit z + 10)) => (->Zipper '(19) (reverse (range 9)))))

(fact "insert-right"
      (fact "At beginning"
            (insert-right zipper :a) => (->Zipper (seq (concat '(0 :a) (range 1 10))) '()))
      (fact "In middle"
            (as-> zipper z ((rights 5) z) (insert-right z :a)) =>
            (->Zipper (cons 5 (cons :a (range 6 10))) (reverse (range 5))))
      (fact "At end"
            (as-> zipper z ((rights 9) z) (insert-right z :a)) =>
            (->Zipper '(9 :a) (reverse (range 9)))))

(fact "insert-left"
      (fact "At beginning"
            (insert-left zipper :a) => (->Zipper (seq (range 10)) '(:a)))
      (fact "In middle"
            (as-> zipper z ((rights 5) z) (insert-left z :a)) =>
            (->Zipper (range 5 10) '(:a 4 3 2 1 0)))
      (fact "At end"
            (as-> zipper z ((rights 9) z) (insert-left z :a)) =>
            (->Zipper '(9) (cons :a (reverse (range 9))))))

(fact "head"
      (fact "At beginning"
            (head zipper) => (seq (range 10)))
      (fact "In middle"
            (as-> zipper z ((rights 5) z) (head z)) => (seq (range 10))))

(fact "comprehensive"
      (fact (as-> zipper z
                  ((rights 5) z)
                  (insert-right z :right) ; '(0 1 2 3 4 5 :right 6 7 8 9)
                  (insert-left z :left)   ; '(0 1 2 3 4 :left 5 :right 6 7 8 9)
                  ((rights 3) z)
                  (replace z :replace1)   ; '(0 1 2 3 4 :left 5 :right 6 :replace1 8 9)
                  ((rights 2) z)
                  (replace z :replace2)   ; '(0 1 2 3 4 :left 5 :right 6 :replace1 8 :replace2)
                  ((lefts 11) z)
                  (edit z + 10)           ; '(10 1 2 3 4 :left 5 :right 6 :replace1 8 :replace2)
                  (head z))
            =>
            '(10 1 2 3 4 :left 5 :right 6 :replace1 8 :replace2)))
