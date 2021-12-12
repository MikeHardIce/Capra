(ns capra.core
  (:import [java.awt Color Dimension Graphics2D Canvas BorderLayout Rectangle BasicStroke]
           [java.awt.event InputEvent ComponentEvent KeyAdapter KeyEvent MouseAdapter MouseEvent MouseMotionAdapter WindowAdapter WindowEvent]
           [java.awt.geom Ellipse2D Ellipse2D$Double Line2D Line2D$Double Path2D Rectangle2D Point2D Arc2D]
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

(defn draw-rect 
  [^Canvas canvas x y width height color fill?]
  (let [^Rectangle2D rec (Rectangle. x y width height)
        ^Graphics2D graphics (.getGraphics canvas)]
    (.setColor graphics color)
    (if fill? 
      (.fill graphics rec)
      (.draw graphics rec))
    canvas))

(defn draw-line
  ([^Canvas canvas x0 y0 x1 y1 color] (draw-line canvas x0 y0 x1 y1 color 1.0))
  ([^Canvas canvas x0 y0 x1 y1 color thickness]
  (let [^Line2D line (Line2D$Double. x0 y0 x1 y1)
        ^Graphics2D graphics (.getGraphics canvas)]
    (.setStroke graphics (BasicStroke. thickness))
    (.setColor graphics color)
    (.draw graphics line))
  canvas))

(defn draw-ellipse
  [^Canvas canvas x y width height color fill?]
  (let [[x0 y0] [(- x (/ width 2)) (- y (/ height 2))]
        ^Ellipse2D circle (Ellipse2D$Double. x0 y0 width height)
        ^Graphics2D graphics (.getGraphics canvas)]
    (.setColor graphics color)
    (.setStroke graphics (BasicStroke. 1))
    (if fill?
      (.fill graphics circle)
      (.draw graphics circle)))
  canvas)