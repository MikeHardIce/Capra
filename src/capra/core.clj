(ns capra.core
  (:import [java.awt Color Shape Dimension Graphics2D Canvas BorderLayout Rectangle BasicStroke]
           [java.awt.event InputEvent ComponentEvent KeyAdapter KeyEvent MouseAdapter MouseEvent MouseMotionAdapter WindowAdapter WindowEvent]
           [java.awt.geom Ellipse2D Ellipse2D$Double Line2D Line2D$Double Rectangle2D]
           [javax.swing ImageIcon JFrame]))

(set! *warn-on-reflection* true)

(defn create-window
  "Creates and displays a window. Returns a map consisting of the window and the canvas"
  ([x y width height title] (create-window x y width height title Color/white false))
  ([x y width height title color] (create-window x y width height title color false))
  ([x y width height title color resizable?]
  (let [dimension (Dimension. width height)
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
    (doto canvas
      (.requestFocus))
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