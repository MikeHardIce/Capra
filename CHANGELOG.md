
[back](https://github.com/MikeHardIce/Capra)

### 0.0.7

* fixed bug in properties function expecting the context to contain a :window key instead of a :frame key

### 0.0.6

* **make draw->, use-buffer->, get-text-dimensions and attach-buffered-strategy use the context returned from the create-window function**
* **renamed key :window to :frame in map that returns for create-window**

### 0.0.5

* fix properties function
* fix get-text-dimension not distinguishing between graphics and buffer

### 0.0.4

* support multiple windows
* **signature of create-window changed! Now with name x y width height title and a map of options like :color :resizable? :icon-path :on-exit**

### 0.0.3

* support double buffering, but so that one could draw multiple times before the buffer is flipped

### 0.0.2

* fix text sizing not working

### 0.0.1

* added type hints to remove need for relection
* support of java.awt.RenderingHints -> the context create by create-window
  has now an additional key called :rendering which can contain a map with
  the RenderingHints Key and Value like {RenderingHints/KEY_ANTIALIASING RenderingHints/VALUE_ANTIALIAS_ON}

### 0.0.1-alpha4

* added properties function which returns a map of the window/canvas properties
* clear-rect function to clear out a rectangular area with the current background color

### 0.0.1-alpha3

* expose key events like tab, shift etc. 
* support bold and italic style when drawing text

### 0.0.1-alpha2

* close-window function to close the window directly

### 0.0.1-alpha1

* events via multi method handle-event
* mouse events: :mouse-dragged :mouse-moved :mouse-pressed :mouse-released
* key events: :key-pressed :key-released
* basic shapes: line rectangle ellipse text