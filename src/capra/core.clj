(ns capra.core
  (:require [clojure.java.io])
  (:import [java.awt Color Shape Dimension Graphics2D Canvas BorderLayout Rectangle BasicStroke]
           [java.awt.event InputEvent ComponentEvent KeyAdapter KeyEvent MouseAdapter MouseEvent MouseMotionAdapter WindowAdapter WindowEvent]
           [java.awt.geom Ellipse2D Ellipse2D$Double Line2D Line2D$Double Rectangle2D]
           [javax.swing ImageIcon JFrame]))

(set! *warn-on-reflection* true)

(defmulti handle-event (fn [action event]
                         action))

(defmethod handle-event :default [action event]
  (println "Event: " action " Map: " event))

(defn create-window
  "Creates and displays a window. Returns a map consisting of the window and the canvas"
  ([x y width height title] (create-window x y width height title Color/white false nil))
  ([x y width height title color] (create-window x y width height title color false nil))
  ([x y width height title color resizable?] (create-window x y width height title color resizable? nil))
  ([x y width height title color resizable? icon-path]
  (let [dimension (Dimension. width height)
        mouse-events (proxy [MouseAdapter] []
                       (mousePressed [^MouseEvent event] (handle-event :mouse-pressed {:button (.getButton event) :x (.getX event) :y (.getY event)}))
                       (mouseReleased [^MouseEvent event] (handle-event :mouse-released {:button (.getButton event) :x (.getX event) :y (.getY event)})))
        mouse-motion-events (proxy [MouseMotionAdapter] []
                              (mouseMoved [^MouseEvent event] (handle-event :mouse-moved {:x (.getX event) :y (.getY event)}))
                              (mouseDragged [^MouseEvent event] (handle-event :mouse-dragged {:x (.getX event) :y (.getY event)})))
        key-events (proxy [KeyAdapter] []
                     (keyPressed [^KeyEvent event] (handle-event :key-pressed {:char (.getKeyChar event) :code (.getKeyCode event)}))
                     (keyReleased [^KeyEvent event] (handle-event :key-released {:char (.getKeyChar event) :code (.getKeyCode event)})))
        canvas (doto (Canvas.)
                 (.setName title)
                 (.setPreferredSize dimension)
                 (.setBackground color)
                 (.setIgnoreRepaint true))
        window (JFrame.)]
    (doto window
      (.setLayout (BorderLayout.))
      (.add canvas)
      (.setSize dimension)
      (.setResizable resizable?)
      (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE)
      ;(.addWindowListener (fn [frame _] (.dispose frame)))
      (.setName title)
      (.setTitle title)
      (.setLocation x y)
      (.setVisible true))
    (when (and (seq icon-path)
               (some #(.endsWith ^String icon-path %) [".png" ".gif" ".jpeg"])
               (.exists (clojure.java.io/file icon-path)))
      (.setIconImage window (.getImage (ImageIcon. ^String icon-path))))
    (doto canvas
      (.requestFocus)
      (.addMouseListener mouse-events)
      (.addMouseMotionListener mouse-motion-events)
      (.addKeyListener key-events))
    {:window window :canvas canvas})))

(declare ^:dynamic *graphics*)

(defn- draw 
  [^Graphics2D graphics ^Shape shape color fill? thickness]
    (.setColor graphics color)
    (.setStroke graphics (BasicStroke. thickness))
    (if fill?
      (.fill graphics shape)
      (.draw graphics shape))
  graphics)

(defmacro draw-> 
  [canvas & body]
  `(binding [^Graphics2D *graphics* (.getGraphics ~canvas)]
    ~@body
    (.dispose *graphics*)))

(defn rect 
  ([x y width height color fill?] (rect x y width height color fill? 1))
  ([x y width height color fill? thickness]
  (let [^Rectangle2D rec (Rectangle. x y width height)]
    (draw *graphics* rec color fill? thickness))))

(defn line
  ([x0 y0 x1 y1 color] (line x0 y0 x1 y1 color 1))
  ([x0 y0 x1 y1 color thickness]
  (let [^Line2D line (Line2D$Double. x0 y0 x1 y1)]
    (draw *graphics* line color nil thickness))))

(defn ellipse
  ([x y width height color fill?] (ellipse x y width height color fill? 1))
  ([x y width height color fill? thickness]
  (let [[x0 y0] [(- x (/ width 2)) (- y (/ height 2))]
        ^Ellipse2D circle (Ellipse2D$Double. x0 y0 width height)]
    (draw *graphics* circle color fill? thickness))))

(defn get-text-dimensions
  "Gets the width and the height of a given text"
  [^Canvas canvas text font-size]
  (let [^Graphics2D gr (.getGraphics canvas)
        ^java.awt.Font font (.getFont gr)
        ^java.awt.Font font (.deriveFont font (float font-size))]
    (.setFont gr font)
    (let [box (.getStringBounds (.getFontMetrics gr) (str text) gr)]
      [(.getWidth box) (.getHeight box)])))

(defn text
  [^Integer x ^Integer y ^String content color font-size]
  (let [^Graphics2D gr *graphics*
        ^java.awt.Font font (.getFont gr)
        ^java.awt.Font font (.deriveFont font (float font-size))]
    (.setColor gr color)
    (.setFont gr font)
    (.drawString gr content x y)))