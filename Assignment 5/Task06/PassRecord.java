import java.math.BigDecimal;
import java.time.LocalDate;

public record PassRecord(
        String passId,
        String registerNumber,
        String name,
        String routeNumber,
        String boardingStop,
        LocalDate validityDate,
        BigDecimal amountPaid) {

    public PassRecord {
        if (passId == null || !passId.matches("P\\d{4,}")) {
            throw new IllegalArgumentException("Invalid pass ID");
        }
        if (registerNumber == null || !registerNumber.matches("\\d{9}")) {
            throw new IllegalArgumentException("Invalid register number");
        }
        if (name == null || name.isBlank() || routeNumber == null || routeNumber.isBlank()
                || boardingStop == null || boardingStop.isBlank() || validityDate == null
                || amountPaid == null || amountPaid.signum() < 0) {
            throw new IllegalArgumentException("Invalid pass field");
        }
    }

    public static PassRecord parse(String line) {
        String[] fields = line.split("\\|", -1);
        if (fields.length != 7) {
            throw new IllegalArgumentException("Expected 7 fields, found " + fields.length);
        }
        return new PassRecord(fields[0].trim(), fields[1].trim(), fields[2].trim(),
                fields[3].trim(), fields[4].trim(), LocalDate.parse(fields[5].trim()),
                new BigDecimal(fields[6].trim()));
    }

    public String toDelimitedLine() {
        return String.join("|", passId, registerNumber, name, routeNumber,
                boardingStop, validityDate.toString(), amountPaid.toPlainString());
    }
}
