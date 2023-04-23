(ns capra.core
  (:require [clojure.java.io])
  (:import [java.awt Color Shape Dimension Graphics2D Canvas BorderLayout Rectangle BasicStroke]
           [java.awt.event WindowListener WindowEvent ComponentEvent ComponentListener KeyAdapter KeyEvent MouseAdapter MouseEvent MouseMotionAdapter]
           [java.awt.geom Ellipse2D Ellipse2D$Double Line2D Line2D$Double Rectangle2D]
           [java.awt.image BufferStrategy]
           [javax.swing ImageIcon JFrame]))

(set! *warn-on-reflection* true)

(defonce ^:const exit JFrame/EXIT_ON_CLOSE)
(defonce ^:const hide JFrame/HIDE_ON_CLOSE)

(defmulti handle-event (fn [action event]
                         action))

(defmethod handle-event :default [action event])

;; TODO: event on window close/hide
(defn create-window
  "Creates and displays a window. Returns a map consisting of the window and the canvas"
  [name x y width height title {:keys [color resizable? icon-path on-close hide-title-bar?] :or {color Color/white
                                                                                                 resizable? false
                                                                                                 icon-path nil
                                                                                                 on-close exit
                                                                                                 hide-title-bar? false}}]
  (let [dimension (Dimension. width height)
        mouse-events (proxy [MouseAdapter] []
                       (mousePressed [^MouseEvent event] (handle-event :mouse-pressed {:button (.getButton event) :x (.getX event) :y (.getY event) :window-name name}))
                       (mouseReleased [^MouseEvent event] (handle-event :mouse-released {:button (.getButton event) :x (.getX event) :y (.getY event) :window-name name})))
        mouse-motion-events (proxy [MouseMotionAdapter] []
                              (mouseMoved [^MouseEvent event] (handle-event :mouse-moved {:x (.getX event) :y (.getY event) :window-name name}))
                              (mouseDragged [^MouseEvent event] (handle-event :mouse-dragged {:x (.getX event) :y (.getY event) :window-name name})))
        key-events (proxy [KeyAdapter] []
                     (keyPressed [^KeyEvent event] (handle-event :key-pressed {:char (.getKeyChar event) :code (.getKeyCode event) :window-name name}))
                     (keyReleased [^KeyEvent event] (handle-event :key-released {:char (.getKeyChar event) :code (.getKeyCode event) :window-name name})))
        window-events (proxy [ComponentListener] []
                        (componentHidden [^ComponentEvent event] (handle-event :window-closed {:window-name name}))
                        (componentMoved [^ComponentEvent event] (let [component (.getComponent event)
                                                                      location (.getLocation component)
                                                                      [x y w h] [(.getX location) (.getY location) (.getWidth component) (.getHeight component)]]
                                                                  (handle-event :window-moved {:x x :y y :width w :height h :window-name name})))
                        (componentResized [^ComponentEvent event] (let [component (.getComponent event)
                                                                        location (.getLocation component)
                                                                        [x y w h] [(.getX location) (.getY location) (.getWidth component) (.getHeight component)]]
                                                                    (handle-event :window-resized {:x x :y y :width w :height h :window-name name})))
                        (componentShown [^ComponentEvent event] #_(handle-event :window-shown {:window-name name})))
        frame-events (proxy [WindowListener] []
                       (windowClosed [^WindowEvent event] (handle-event :window-closed {:window-name name}))
                       (windowIconified [^WindowEvent event] (handle-event :window-hidden {:window-name name}))
                       (windowDeiconified [^WindowEvent event] (handle-event :window-shown {:window-name name}))
                       (windowActivated [^WindowEvent event] (handle-event :window-focused {:window-name name}))
                       (windowClosing [^WindowEvent event])
                       (windowDeactivated [^WindowEvent event] (handle-event :window-unfocused {:window-name name}))
                       (windowOpened [^WindowEvent event]))
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
      (.setDefaultCloseOperation on-close)
      (.setName name)
      (.setTitle title)
      (.setLocation x y)
      (.setUndecorated hide-title-bar?)
      (.setVisible true)
      (.addWindowListener frame-events)
      (.addComponentListener window-events))
    (when (and (seq icon-path)
               (some #(.endsWith ^String icon-path %) [".png" ".gif" ".jpeg"])
               (.exists (clojure.java.io/file icon-path)))
      (.setIconImage window (.getImage (ImageIcon. ^String icon-path))))
    (doto canvas
      (.requestFocus)
      (.addMouseListener mouse-events)
      (.addMouseMotionListener mouse-motion-events)
      (.addKeyListener key-events)
      (.setFocusTraversalKeysEnabled false))
    {:frame window :canvas {:canvas canvas :rendering {}}}))

(defn attach-buffered-strategy
  [window buffer-count]
  (let [canvas (:canvas window)
        canvas (if (:has-buffer? canvas)
                 canvas
                 (do 
                   (.createBufferStrategy ^Canvas (:canvas canvas) buffer-count)
                   (assoc canvas :has-buffer? true)))]
    (assoc window :canvas canvas)))

(defn properties 
  "Gets the properties of the context, which is the map returned by 'create-window' function"
  [{:keys [^JFrame window canvas]}]
  {:x (.getX window) :y (.getY window) 
   :width (.getWidth window) :height (.getHeight window)
   :title (.getName window) :color (.getBackground ^Canvas (:canvas canvas))
   :resizable? (.isResizable window)
   :icon-path (first (.getIconImages window))})

(defn close-window 
  [window]
  (let [window ^JFrame (:frame window)]
    (.dispatchEvent window (java.awt.event.WindowEvent. window java.awt.event.WindowEvent/WINDOW_CLOSING))))

(declare ^:dynamic ^BufferStrategy *strategy*)
(declare ^:dynamic ^Graphics2D *graphics*)

(defn- draw 
  [^Graphics2D graphics ^Shape shape color fill? thickness]
    (.setColor graphics color)
    (.setStroke graphics (BasicStroke. thickness))
    (if fill?
      (.fill graphics shape)
      (.draw graphics shape))
  graphics)

(defmacro use-buffer->
  [window & body]
  `(let [strategy# (.getBufferStrategy ^Canvas (-> ~window :canvas :canvas))]
     (binding [*strategy* strategy#]
       ~@body
       (.show *strategy*))))

(defmacro draw-> 
  [window & body]
  `(let [graph# (if (bound? #'*strategy*)
                  (.getDrawGraphics *strategy*)
                  (.getGraphics ^Canvas (-> ~window :canvas :canvas)))
         ^Graphics2D graphics# (loop [g# ^Graphics2D graph#
                                      hints# (-> ~window :canvas :rendering)]
                                 (if (seq hints#)
                                   (recur (doto g#
                                            (.setRenderingHint (-> hints# first key) (-> hints# first val)))
                                          (rest hints#))
                                   g#))]
    (binding [*graphics* graphics#]
      ~@body
      (.dispose ^Graphics2D *graphics*))))

(defn rect 
  ([x y width height color fill?] (rect x y width height color fill? 1))
  ([x y width height color fill? thickness]
  (let [^Rectangle2D rec (Rectangle. x y width height)]
    (draw *graphics* rec color fill? thickness))))

(defn clear-rect 
  "Fills a rectangular shape with the current background color"
  [x y width height]
  (.clearRect ^Graphics2D *graphics* x y width height))

(defn line
  ([x0 y0 x1 y1 color] (line x0 y0 x1 y1 color 1))
  ([x0 y0 x1 y1 color thickness]
  (let [^Line2D line (Line2D$Double. x0 y0 x1 y1)]
    (draw ^Graphics2D *graphics* line color nil thickness))))

(defn ellipse
  ([x y width height color fill?] (ellipse x y width height color fill? 1))
  ([x y width height color fill? thickness]
  (let [[x0 y0] [(- x (/ width 2)) (- y (/ height 2))]
        ^Ellipse2D circle (Ellipse2D$Double. x0 y0 width height)]
    (draw ^Graphics2D *graphics* circle color fill? thickness))))

(defn get-text-dimensions
  "Gets the width and the height of a given text"
  [window text font-size]
  (let [^Graphics2D gr (if (bound? #'*strategy*)
                         (.getDrawGraphics *strategy*)
                         (.getGraphics ^Canvas (-> window :canvas :canvas)))
        ^java.awt.Font font (.getFont gr)
        ^java.awt.Font font (.deriveFont font (float font-size))]
    (.setFont gr font)
    (let [box (.getStringBounds (.getFontMetrics gr) (str text) gr)]
      [(.getWidth box) (.getHeight box)])))

(defn text
  ([^Integer x ^Integer y ^String content color font-size]
   (text x y content color font-size nil))
  ([^Integer x ^Integer y ^String content color font-size style]
   (let [^Graphics2D gr *graphics*
        font (.getFont gr)
        font (.deriveFont font (float font-size))
        font (.deriveFont font (cond 
                                     (= style :bold) ^Integer java.awt.Font/BOLD
                                     (= style :italic) ^Integer java.awt.Font/ITALIC
                                     :else ^Integer java.awt.Font/PLAIN))]
     (.setColor gr color)
     (.setFont gr font)
     (.drawString gr content x y))))