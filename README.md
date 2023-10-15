# Capra

[![Clojars Project](https://img.shields.io/clojars/v/com.github.mikehardice/capra.svg)](https://clojars.org/com.github.mikehardice/capra)

Little drawing library that uses Java AWT underneath.


[See Changes](CHANGELOG.md)

Use via 
```Clojure
[com.github.mikehardice/capra "0.0.9"]
```
in your project.clj

The following shows a little example how to create a window and draw a few basic shapes.
```Clojure
(ns capra.playground
  (:require [capra.core :refer [ellipse rect line text create-window draw-> get-text-dimensions handle-event]])
  (:import [java.awt Color RenderingHints]))

(defn draw-weigth
  [window x y]
  (draw-> window
          (ellipse x y 100 110 Color/red true)
          (ellipse x y 90 100 Color/green true)
          (ellipse x y 80 90 Color/red true)
          (ellipse x y 70 80 Color/green true)))

(defn main []
  (let [window (create-window "main" 200 100 500 600 "Meeehhhh" {:color Color/black :resizable? true :icon-path "resources/icon-test.bla" :on-close exit})
        window (assoc-in window [:canvas :rendering] {RenderingHints/KEY_ANTIALIASING RenderingHints/VALUE_ANTIALIAS_ON})]
    (doto (:frame window)
      (.setLocation 300 200))
    (draw-> window
                          (rect 100 100 50 60 Color/yellow true)
                          (rect 300 100 50 60 Color/black false)
                          (rect 100 300 50 60 Color/green true)
                          (rect 300 300 50 60 Color/orange true)
                          (line 200 200 400 400 Color/green 5)
                          (text 200 50 "Hello this is a test" Color/pink 20))
    (draw-weigth window 400 400)
    (draw-weigth window 200 200)
    (println "Size: " (get-text-dimensions window "Hello this is a test" 20))))

```
There are a few **window events** that are currently supported
```Clojure
(defmethod handle-event :mouse-dragged [_ event]
  (println "mouse at (" (:x event) " , " (:y event) ") is being dragged on window " (:window-name event)))

(defmethod handle-event :key-pressed [_ {:keys [code char window-name]}]
  (println "key pressed " code " (code) " char " (char) on window " window-name))

(defmethod handle-event :window-shown [_ {:keys [window-name]}]
  (println "window shown " window-name))

(defmethod handle-event :window-resized [_ {:keys [x y width height window-name]}]
  (println "window resized: " x " " y " " width " " height " on window " window-name))

(defmethod handle-event :window-moved [_ {:keys [x y width height window-name]}]
  (println "window moved: " x " " y " " width " " height " on window " window-name))

(defmethod handle-event :window-hidden [_ {:keys [window-name]}]
  (println "window hidden " window-name))

(defmethod handle-event :window-focused [_ {:keys [window-name]}]
  (println "window focus " window-name))

(defmethod handle-event :window-unfocused [_ {:keys [window-name]}]
  (println "window unfocused " window-name))

```

Capra supports **double buffering**. To let all draw-> calls draw to the same buffer, use use-buffer-> which will flip the buffer at the end.
Example similar as above, but now with double buffering:

```Clojure
(defn main []
  (let [window (create-window "main" 200 100 500 600 "Meeehhhh" {:color Color/black :resizable? true :icon-path "resources/icon-test.bla" :on-close exit})
        window (assoc-in window [:canvas :rendering] {RenderingHints/KEY_ANTIALIASING RenderingHints/VALUE_ANTIALIAS_ON})
        window (attach-buffered-strategy window 2)]
    (use-buffer-> window
     (draw-> window
             (rect 100 100 50 60 Color/yellow true)
             (rect 300 100 50 60 Color/black false)
             (rect 100 300 50 60 Color/green true)
             (rect 300 300 50 60 Color/orange true)
             (line 200 200 400 400 Color/green 5)
             (text 200 50 "Hello this is a test" Color/pink 20))
     (draw-weigth window 400 400)
     (draw-weigth window 200 200))))
```

There is now support for creating and drawing to **multiple windows**.
```Clojure
(ns capra.playground
  (:require [capra.core :refer [ellipse rect line text create-window draw-> use-buffer-> get-text-dimensions handle-event attach-buffered-strategy exit hide properties]])
  (:import [java.awt Color RenderingHints]))

(defn draw-weigth
  [window x y]
  (draw-> window
          (ellipse x y 100 110 Color/red true)
          (ellipse x y 90 100 Color/green true)
          (ellipse x y 80 90 Color/red true)
          (ellipse x y 70 80 Color/green true)))

(defn main []
  (let [window (create-window "main" 200 100 500 600 "Meeehhhh" {:color Color/white :resizable? true :icon-path "resources/icon-test.bla" :on-close exit})
        sub-window (create-window "not-main" 200 100 500 600 "This is not Main" {:color Color/black :resizable? false :icon-path "resources/icon-test.bla" :on-close hide :hide-title-bar? true})
        sub-window (attach-buffered-strategy sub-window 2)
        window (assoc-in window [:canvas :rendering] {RenderingHints/KEY_ANTIALIASING RenderingHints/VALUE_ANTIALIAS_ON})
        window (attach-buffered-strategy window 2)]
    (doto (:frame window)
      (.setLocation 300 200))
    (use-buffer-> window
                  (draw-> window
                          (rect 100 100 50 60 Color/yellow true)
                          (rect 300 100 50 60 Color/black false)
                          (rect 100 300 50 60 Color/green true)
                          (rect 300 300 50 60 Color/orange true)
                          (line 200 200 400 400 Color/green 5)
                          (text 200 50 "Hello this is a test" Color/pink 20))
                  (draw-weigth window 400 400)
                  (draw-weigth window 200 200)) 
    (use-buffer-> sub-window
                  (draw-> sub-window
                          (rect 100 100 50 60 Color/yellow true)
                          (rect 300 100 50 60 Color/pink false)
                          (rect 100 300 50 60 Color/pink true)
                          (rect 300 300 50 60 Color/orange true)
                          (line 200 200 400 400 Color/green 5)))
    (println "Size: " (get-text-dimensions sub-window "Hello this is a test" 20))
    (println (properties window))))
```

**The content of a canvas won't be redrawn when the window hides and is shown again. The consuming code would need to take care of this atm.**