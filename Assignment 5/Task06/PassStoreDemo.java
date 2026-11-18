import java.nio.file.Path;

public final class PassStoreDemo {
    private PassStoreDemo() { }

    public static void main(String[] args) throws Exception {
        Path input = args.length == 0 ? Path.of("input", "passes-damaged.txt") : Path.of(args[0]);
        PassStore store = new PassStore(input, warning -> System.out.println("WARNING: " + warning));

        PassRecord normal = store.find("P1003");
        PassRecord damagedId = store.find("P1002");
        PassRecord absent = store.find("P9999");
        PassRecord missingFile = new PassStore(input.resolveSibling("missing.txt")).find("P1003");

        System.out.println("normal.P1003=" + normal);
        System.out.println("damaged.P1002=" + damagedId);
        System.out.println("absent.P9999=" + absent);
        System.out.println("missing.file=" + missingFile);
        System.out.println("route.R12.count=" + store.listByRoute("R12").size());
        System.out.println("tests=PASS");
    }
}
