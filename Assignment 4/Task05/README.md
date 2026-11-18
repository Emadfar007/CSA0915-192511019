# Task 05 - Same Screen, Different Event Styles

`CurrencyConverterStyles.java` implements all four requested styles: panel implements `ActionListener`, named inner `Handler`, anonymous inner classes, and lambdas. Every style registers the amount field too, so Enter converts and shows a dialog during live use. `WindowAdapter.windowClosing` disposes the frame.

| Style | Handler boilerplate | Field access | Reuse | Change impact |
|---|---:|---|---|---|
| Implements listener | medium | direct | low; mixes panel and command | central switch grows |
| Named inner class | medium | direct | high within screen | isolated named unit |
| Anonymous class | high | direct | low | verbose at each source |
| Lambda | low | direct/effectively final | moderate | concise for one action |

The demo instantiates all four and executes the lambda version. Valid input produces `100.00 USD = 8325.00 INR`; invalid `abc` produces a clear error screenshot. A method reference such as `this::handleConvert` is ideal when a named method already exists. A shared `Action` is preferable when menu, toolbar, button and shortcut must share text, enabled state and behavior.
