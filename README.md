# Capra

[![Clojars Project](https://img.shields.io/clojars/v/com.github.mikehardice/capra.svg)](https://clojars.org/com.github.mikehardice/capra)

Little drawing library that uses Java AWT underneath.


[See Changes](CHANGELOG.md)

Use via 
```Clojure
[com.github.mikehardice/capra "0.0.4"]
```
in your project.clj

The following shows a little example how to create a window and draw a few basic shapes.
```Clojure
(ns capra.playground
  (:require [capra.core :refer [ellipse rect line text create-window draw-> get-text-dimensions handle-event]])
  (:import [java.awt Color RenderingHints]))

(defn draw-weigth
  [canvas x y]
  (draw-> canvas
          (ellipse x y 100 110 Color/red true)
          (ellipse x y 90 100 Color/green true)
          (ellipse x y 80 90 Color/red true)
          (ellipse x y 70 80 Color/green true)))

(defn main []
  (let [context (create-window "main" 200 100 500 600 "Meeehhhh" {:color Color/black :resizable? true :icon-path "resources/icon-test.bla" :on-close exit})
        canvas (assoc (:canvas context) :rendering {RenderingHints/KEY_ANTIALIASING RenderingHints/VALUE_ANTIALIAS_ON})]
    (doto (:window context)
      (.setLocation 300 200))
    (draw-> canvas
                          (rect 100 100 50 60 Color/yellow true)
                          (rect 300 100 50 60 Color/black false)
                          (rect 100 300 50 60 Color/green true)
                          (rect 300 300 50 60 Color/orange true)
                          (line 200 200 400 400 Color/green 5)
                          (text 200 50 "Hello this is a test" Color/pink 20))
    (draw-weigth canvas 400 400)
    (draw-weigth canvas 200 200)
    (println "Size: " (get-text-dimensions canvas "Hello this is a test" 20))
    canvas))
```
There are a few window events that are currently supported
```Clojure
(defmethod handle-event :mouse-dragged [_ event]
  (println "mouse at (" (:x event) " , " (:y event) ") is being dragged on window " (:window event)))

(defmethod handle-event :key-pressed [_ {:keys [code char window]}]
  (println "key pressed " code " (code) " char " (char) on window " window))

(defmethod handle-event :window-shown [_ {:keys [window]}]
  (println "window shown " window))

(defmethod handle-event :window-resized [_ {:keys [x y width height window]}]
  (println "window resized: " x " " y " " width " " height " on window " window))

(defmethod handle-event :window-moved [_ {:keys [x y width height window]}]
  (println "window moved: " x " " y " " width " " height " on window " window))

(defmethod handle-event :window-hidden [_ {:keys [window]}]
  (println "window hidden " window))

(defmethod handle-event :window-focused [_ {:keys [window]}]
  (println "window focus " window))

(defmethod handle-event :window-unfocused [_ {:keys [window]}]
  (println "window unfocused " window))
```

Capra supports **double buffering**. To let all draw-> calls draw to the same buffer, use use-buffer-> which will flip the buffer at the end.
Example similar as above, but now with double buffering:

```Clojure
(defn main []
  (let [context (create-window "main" 200 100 500 600 "Meeehhhh" {:color Color/black :resizable? true :icon-path "resources/icon-test.bla" :on-close exit})
        canvas (assoc (:canvas context) :rendering {RenderingHints/KEY_ANTIALIASING RenderingHints/VALUE_ANTIALIAS_ON})
        canvas (attach-buffered-strategy canvas 2)]
    (use-buffer-> canvas
     (draw-> canvas
             (rect 100 100 50 60 Color/yellow true)
             (rect 300 100 50 60 Color/black false)
             (rect 100 300 50 60 Color/green true)
             (rect 300 300 50 60 Color/orange true)
             (line 200 200 400 400 Color/green 5)
             (text 200 50 "Hello this is a test" Color/pink 20))
     (draw-weigth canvas 400 400)
     (draw-weigth canvas 200 200))
    canvas))
```

There is now support for creating and drawing to **multiple windows**.
```Clojure
(ns capra.playground
  (:require [capra.core :refer [ellipse rect line text create-window draw-> use-buffer-> get-text-dimensions handle-event attach-buffered-strategy exit hide]])
  (:import [java.awt Color RenderingHints]))

(defn draw-weigth
  [canvas x y]
  (draw-> canvas
          (ellipse x y 100 110 Color/red true)
          (ellipse x y 90 100 Color/green true)
          (ellipse x y 80 90 Color/red true)
          (ellipse x y 70 80 Color/green true)))

(defn main []
  (let [context (create-window "main" 200 100 500 600 "Meeehhhh" {:color Color/black :resizable? true :icon-path "resources/icon-test.bla" :on-close exit})
        context2 (create-window "not-main" 200 100 500 600 "This is not Main" {:color Color/black :resizable? false :icon-path "resources/icon-test.bla" :on-close hide :hide-title-bar? true})
        canvas2 (attach-buffered-strategy (:canvas context2) 2)
        canvas (assoc (:canvas context) :rendering {RenderingHints/KEY_ANTIALIASING RenderingHints/VALUE_ANTIALIAS_ON})
        canvas (attach-buffered-strategy canvas 2)]
    (use-buffer-> canvas
     (draw-> canvas
             (rect 100 100 50 60 Color/yellow true)
             (rect 300 100 50 60 Color/black false)
             (rect 100 300 50 60 Color/green true)
             (rect 300 300 50 60 Color/orange true)
             (line 200 200 400 400 Color/green 5)
             (text 200 50 "Hello this is a test" Color/pink 20))
     (draw-weigth canvas 400 400)
     (draw-weigth canvas 200 200))
    (use-buffer-> canvas2
                  (draw-> canvas2
                          (rect 100 100 50 60 Color/yellow true)
                          (rect 300 100 50 60 Color/pink false)
                          (rect 100 300 50 60 Color/pink true)
                          (rect 300 300 50 60 Color/orange true)
                          (line 200 200 400 400 Color/green 5)))
    canvas))
```