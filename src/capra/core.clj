(ns capra.core
  (:import [java.awt Color Dimension Graphics2D Canvas BorderLayout]
           [java.awt.event InputEvent ComponentEvent KeyAdapter KeyEvent MouseAdapter MouseEvent MouseMotionAdapter WindowAdapter WindowEvent]
           [java.awt.geom Ellipse2D Line2D Path2D Rectangle2D Point2D Arc2D]
           [javax.swing ImageIcon JFrame]))

(defn create-window
  ""
  [x y width height title color]
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
      (.setResizable true)
      (.setName title)
      (.setTitle title)
      (.setLocation x y)
      (.setVisible true))
    (doto canvas
      (.requestFocus))
    {:window window :canvas canvas}))