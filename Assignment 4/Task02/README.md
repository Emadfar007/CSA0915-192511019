# Task 02 - Requirement to Event-Driven Design

## Event table

| Source | Event object | Listener | Action |
|---|---|---|---|
| Department combo | `ActionEvent` | `ActionListener` | Replace doctor model from department Map |
| Book button | `ActionEvent` | `ActionListener` | Validate patient/date and book |
| Patient/date field Enter | `ActionEvent` | `ActionListener` | Submit without mouse |
| Window close button | `WindowEvent` | `WindowAdapter` | Dispose resources/window |

## Containment tree

```text
JFrame [BorderLayout]
`-- AppointmentDeskApp.JPanel [BorderLayout]
    |-- NORTH: JLabel title
    |-- CENTER: JPanel form [GridLayout 4x2]
    |   |-- JLabel + JTextField patient
    |   |-- JLabel + JComboBox department
    |   |-- JLabel + JComboBox doctor
    |   `-- JLabel + JTextField date
    `-- SOUTH: JPanel [FlowLayout]
        |-- JLabel status
        `-- JButton Book Appointment
```

## Pseudocode

```text
ON departmentChanged
  department <- selected department
  doctors <- doctorsByDepartment.get(department)
  clear doctor combo
  FOR EACH doctor IN doctors: add doctor
  show number loaded

ON book
  trim patient and parse date
  IF patient invalid OR date before today: show error; RETURN
  persist appointment outside EDT when database-backed
  show booking confirmation
```

Method trace: `main -> invokeLater -> JFrame constructor/configuration -> AppointmentDeskApp constructor -> buildUi -> register listeners -> refreshDoctors -> setVisible`; interaction calls `ActionListener.actionPerformed -> refreshDoctors/book`; close produces `windowClosing -> dispose -> windowClosed`.

An applet container calls `init`, `start`, repeated `paint`, `stop`, then `destroy`; a desktop window is explicitly constructed/shown and receives window events. Loading doctors per change costs a database round-trip and repeated query work; loading once into a Map uses `O(d)` memory and average `O(1)` department lookup, appropriate for a small, slowly changing catalogue.
