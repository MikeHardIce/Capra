
[back](https://github.com/MikeHardIce/Capra

### 0.1.0

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