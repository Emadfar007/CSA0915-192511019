import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class InventoryRedesignBenchmark {
    private static volatile long sink;

    private static final class OriginalInventory {
        private final Map<String, Integer> stock = new HashMap<>();
        void put(String code, int quantity) { stock.put(code, quantity); }
        long codeReportChecksum() {
            List<String> keys = new ArrayList<>(stock.keySet());
            Collections.sort(keys);
            long sum = 0; for (String key : keys) sum += key.hashCode(); return sum;
        }
    }

    private static final class IndexedInventory {
        private final Map<String, Integer> lookup = new HashMap<>();
        private final TreeMap<String, Integer> codeIndex = new TreeMap<>();
        private final LinkedHashMap<String, Integer> insertionIndex = new LinkedHashMap<>();
        void put(String code, int quantity) {
            lookup.put(code, quantity); codeIndex.put(code, quantity); insertionIndex.put(code, quantity);
        }
        long codeReportChecksum() {
            long sum = 0; for (String key : codeIndex.keySet()) sum += key.hashCode(); return sum;
        }
    }

    private static void run(int size, int reports) {
        OriginalInventory original = new OriginalInventory();
        IndexedInventory redesigned = new IndexedInventory();
        long originalInsertStart = System.nanoTime();
        for (int i = size - 1; i >= 0; i--) original.put(String.format("M%06d", i), i);
        long originalInsert = System.nanoTime() - originalInsertStart;
        long redesignedInsertStart = System.nanoTime();
        for (int i = size - 1; i >= 0; i--) redesigned.put(String.format("M%06d", i), i);
        long redesignedInsert = System.nanoTime() - redesignedInsertStart;

        long start = System.nanoTime();
        for (int i = 0; i < reports; i++) sink ^= original.codeReportChecksum();
        long originalReports = System.nanoTime() - start;
        start = System.nanoTime();
        for (int i = 0; i < reports; i++) sink ^= redesigned.codeReportChecksum();
        long redesignedReports = System.nanoTime() - start;
        System.out.printf(Locale.ROOT, "%d,%d,%d,%d,%d,%d,%.2f%n", size, reports,
                originalInsert, redesignedInsert, originalReports, redesignedReports,
                (double) originalReports / Math.max(1, redesignedReports));
    }

    public static void main(String[] args) {
        System.out.println("medicines,reports,original_insert_ns,redesigned_insert_ns,original_reports_ns,redesigned_reports_ns,speedup");
        for (int size : new int[]{1_000, 5_000, 20_000}) run(size, 100);
        System.out.println("sink=" + sink);
    }
}
