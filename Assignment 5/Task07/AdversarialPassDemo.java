import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class AdversarialPassDemo {
    private AdversarialPassDemo() { }

    public static void main(String[] args) throws Exception {
        Path input = args.length == 0 ? Path.of("input", "adversarial-passes.txt") : Path.of(args[0]);
        int accepted = 0;
        int rejected = 0;
        int lineNumber = 0;
        for (String line : Files.readAllLines(input, StandardCharsets.UTF_8)) {
            lineNumber++;
            if (line.isBlank()) {
                rejected++;
                System.out.println("line=" + lineNumber + ",result=REJECTED,reason=empty line");
                continue;
            }
            String[] fields = line.split("\\|", -1);
            if (fields.length != 7) {
                rejected++;
                System.out.println("line=" + lineNumber + ",result=REJECTED,reason=field count " + fields.length);
                continue;
            }
            try {
                BigDecimal amount = new BigDecimal(fields[6]);
                if (amount.signum() < 0) throw new NumberFormatException("negative amount");
                accepted++;
                System.out.println("line=" + lineNumber + ",result=ACCEPTED,id=" + fields[0]);
            } catch (NumberFormatException error) {
                rejected++;
                System.out.println("line=" + lineNumber + ",result=REJECTED,reason=non-numeric amount");
            }
        }
        System.out.println("accepted=" + accepted + ",rejected=" + rejected);
        System.out.println("tests=" + (accepted == 1 && rejected == 3 ? "PASS" : "FAIL"));
    }
}
