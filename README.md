# capra

```
(ns capra.playground
  (:require [capra.core :refer [ellipse rect line create-window draw->]])
  (:import [java.awt Color]))

(defn draw-weigth 
  [canvas x y]
  (draw-> canvas
      (ellipse x y 100 110 Color/red true)
      (ellipse x y 90 100 Color/green true)
      (ellipse x y 80 90 Color/red true)
      (ellipse x y 70 80 Color/green true)))

(defn main []
  (let [context (create-window 200 100 500 600 "Meeehhhh" Color/black)
        canvas (:canvas context)]
    (doto (:window context)
      (.setLocation 300 200))
    (draw-> canvas
            (rect 100 100 50 60 Color/yellow true)
            (rect 300 100 50 60 Color/black false)
            (rect 100 300 50 60 Color/green true)
            (rect 300 300 50 60 Color/orange true)
            (line 200 200 400 400 Color/green 5))
    (draw-weigth canvas 400 400)
    (draw-weigth canvas 200 200)
    canvas))
```
