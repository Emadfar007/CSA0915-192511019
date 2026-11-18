# Task 03 - From Sketch to Layout

## Nested layout

```text
root JPanel [BorderLayout]
|-- NORTH title JLabel
|-- CENTER JPanel [BorderLayout]
|   |-- NORTH searchRow [FlowLayout LEFT]
|   `-- CENTER JScrollPane -> JTable results
`-- SOUTH JPanel [BorderLayout]
    |-- NORTH buttons [FlowLayout CENTER]: Prev, Next, Reserve
    `-- SOUTH status JLabel
```

BorderLayout gives surplus space to the centre, so the results table grows. FlowLayout keeps search controls and action buttons at preferred sizes. GridLayout is suitable for equal forms but would stretch unrelated controls; CardLayout is useful for mutually exclusive screens, not this single search view.

Screenshots at 640x420, 900x560 and 1200x720 show the table viewport expanding while the top row and lower buttons remain stable. Nested layouts are simpler and more maintainable here than one GridBagLayout. GridBagLayout gives precise cross-axis alignment but requires many constraints. Absolute positioning has no layout cost beyond `O(c)` component painting but breaks with font, platform, DPI and window-size changes.
