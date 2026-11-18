# Task 04 - Event-Handling Forensics

The broken handler typically uses the wrong method name/signature (for example `mouseClick` instead of `mouseClicked`) or never registers the listener. The counter may update a local variable instead of the label or mutate Swing outside the EDT. Drawing directly with `getGraphics()` is temporary: repaint after minimise/restore clears it because no model state can reconstruct the picture.

The repair registers a `MouseAdapter`, uses `@Override mouseClicked`, stores every `Point`, updates the label, and calls `repaint()`. `paintComponent()` first calls `super` and redraws the entire point list. `@Override` makes a misspelled listener method a compile-time error instead of a silent unused method.

With p points, storing Points uses `O(p)` memory and repaint is `O(p)`. An off-screen `BufferedImage` uses `O(width x height)` memory, makes each dot update `O(1)`, and repaint copies pixels; it is better for thousands of strokes but must be recreated/rescaled carefully. The evidence images show five points before minimise and the same five after restore.
