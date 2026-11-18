import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class MigrationInputGenerator {
    private MigrationInputGenerator() { }

    public static void main(String[] args) throws Exception {
        Path output = Path.of(args.length > 0 ? args[0] : "migration-100000.txt");
        int validRows = args.length > 1 ? Integer.parseInt(args[1]) : 100_000;
        try (var writer = Files.newBufferedWriter(output, StandardCharsets.UTF_8)) {
            for (int index = 0; index < validRows; index++) {
                if (index == 25_000) writer.write("BROKEN|TOO|FEW\n");
                if (index == 50_000) writer.write("PNEG00001|192511019|Negative|R1|Stop|2027-05-31|-5.00\n");
                if (index == 75_000) writer.write("PBADDATE|192511019|Bad Date|R1|Stop|31-05-2027|1250.00\n");
                writer.write("M" + String.format("%09d", index) + "|"
                        + String.format("%09d", 200_000_000 + index) + "|Migrated Student " + index
                        + "|R" + (index % 40) + "|Campus " + (index % 12) + " Stop " + (index % 80)
                        + "|2027-05-31|1250.00");
                writer.newLine();
            }
        }
        System.out.println("generated_valid_rows=" + validRows);
        System.out.println("generated_invalid_rows=3");
        System.out.println("output=" + output.toAbsolutePath());
    }
}
