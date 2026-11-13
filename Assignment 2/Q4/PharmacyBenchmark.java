import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.function.Supplier;

public class PharmacyBenchmark {
    private static volatile long sink;

    private static Map<String, Integer> inventory(Supplier<Map<String, Integer>> supplier, int size) {
        Map<String, Integer> map = supplier.get();
        for (int i = size - 1; i >= 0; i--) map.put(String.format("M%06d", i), i % 500);
        return map;
    }

    private static long averageLookup(Map<String, Integer> map, int size) {
        Random random = new Random(42);
        String[] keys = new String[100_000];
        for (int i = 0; i < keys.length; i++) keys[i] = String.format("M%06d", random.nextInt(size));
        long total = 0;
        for (int run = 0; run < 5; run++) {
            long start = System.nanoTime();
            long checksum = 0;
            for (String key : keys) checksum += map.get(key);
            total += System.nanoTime() - start;
            sink ^= checksum;
        }
        return total / 5 / keys.length;
    }

    private static long codeReport(Map<String, Integer> map) {
        long start = System.nanoTime();
        Iterable<String> keys;
        if (map instanceof TreeMap) keys = map.keySet();
        else {
            List<String> sorted = new ArrayList<>(map.keySet());
            Collections.sort(sorted); keys = sorted;
        }
        long checksum = 0;
        for (String key : keys) checksum += key.hashCode();
        sink ^= checksum;
        return System.nanoTime() - start;
    }

    private static long insertionReport(Map<String, Integer> map) {
        long start = System.nanoTime();
        long checksum = 0;
        for (String key : map.keySet()) checksum += key.hashCode();
        sink ^= checksum;
        return System.nanoTime() - start;
    }

    private static void run(String type, Supplier<Map<String, Integer>> supplier, int size,
                            boolean codeOrder, boolean insertionOrder) {
        Map<String, Integer> map = inventory(supplier, size);
        long code = 0, insertion = 0;
        for (int i = 0; i < 5; i++) { code += codeReport(map); insertion += insertionReport(map); }
        System.out.printf(Locale.ROOT, "%d,%s,%d,%d,%d,%s,%s%n", size, type,
                averageLookup(map, size), code / 5, insertion / 5, codeOrder, insertionOrder);
    }

    public static void main(String[] args) {
        System.out.println("medicines,map,lookup_avg_ns,code_report_avg_ns,iteration_report_avg_ns,natural_code_order,natural_insertion_order");
        for (int size : new int[]{100, 1_000, 10_000}) {
            run("HashMap", HashMap::new, size, false, false);
            run("TreeMap", TreeMap::new, size, true, false);
            run("LinkedHashMap", LinkedHashMap::new, size, false, true);
        }
        System.out.println("sink=" + sink);
    }
}
