(ns capra.core
  (:import [java.awt Color Dimension Graphics2D Canvas BorderLayout]
           [java.awt.event InputEvent ComponentEvent KeyAdapter KeyEvent MouseAdapter MouseEvent MouseMotionAdapter WindowAdapter WindowEvent]
           [java.awt.geom Ellipse2D Line2D Path2D Rectangle2D Point2D Arc2D]
           [javax.swing ImageIcon JFrame]))

(defn create-window
  "Creates and displays a window. Returns a map consisting of the window and the canvas"
  ([x y width height title] (create-window x y width height title Color/white false))
  ([x y width height title color] (create-window x y width height title color false))
  ([x y width height title color resizable?]
  (let [dimension (Dimension. width height)
        canvas (doto (Canvas.)
                 (.setName title)
                 (.setPreferredSize dimension)
                 (.setBackground color))
        window (JFrame.)]
    (doto window
      (.setLayout (BorderLayout.))
      (.add canvas)
      (.setSize dimension)
      (.setResizable resizable?)
      (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE)
      (.addWindowListener (fn [frame _] (.dispose frame)))
      (.setName title)
      (.setTitle title)
      (.setLocation x y)
      (.setVisible true))
    (doto canvas
      (.requestFocus))
    {:window window :canvas canvas})))