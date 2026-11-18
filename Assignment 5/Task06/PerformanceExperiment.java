import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PerformanceExperiment {
    private static final int MIB = 1024 * 1024;
    private static volatile long blackhole;

    private PerformanceExperiment() { }

    public static void main(String[] args) throws Exception {
        Path work = args.length == 0 ? Path.of("experiment-data") : Path.of(args[0]);
        Files.createDirectories(work);
        System.out.println("LINE_COUNT_RESULTS");
        System.out.println("size_mib,bytes,unbuffered_ms,buffered_ms,lines,java_read_calls_estimate,buffer_refills_estimate");
        for (int sizeMiB : new int[] {1, 10, 50}) {
            Path file = work.resolve("log-v2-" + sizeMiB + "MiB.txt");
            createExactSizeLog(file, sizeMiB * MIB);
            Measurement unbuffered = measureOnce(() -> countLinesUnbuffered(file));
            Measurement buffered = measureOnce(() -> countLinesBuffered(file));
            if (unbuffered.result != buffered.result) {
                throw new IllegalStateException("Line counts differ for " + file);
            }
            System.out.printf("%d,%d,%.3f,%.3f,%d,%d,%d%n", sizeMiB, Files.size(file),
                    unbuffered.millis(), buffered.millis(), buffered.result, Files.size(file) + 1,
                    (Files.size(file) + 65535) / 65536 + 1);
        }

        System.out.println("SEARCH_RESULTS");
        System.out.println("records,single_scan_ms,map_build_ms,single_map_lookup_us,q,scan_q_ms,map_q_ms,map_entries");
        for (int count : new int[] {1_000, 10_000, 100_000}) {
            Path file = work.resolve("passes-" + count + ".txt");
            createPasses(file, count);
            String lastId = idFor(count - 1);
            Measurement scan = measure(() -> scanFor(file, lastId) == null ? 0 : 1);
            long buildStart = System.nanoTime();
            Map<String, String> map = loadMap(file);
            long buildNanos = System.nanoTime() - buildStart;
            long lookupStart = System.nanoTime();
            String found = map.get(lastId);
            long lookupNanos = System.nanoTime() - lookupStart;
            if (found == null) {
                throw new IllegalStateException("Map lookup failed");
            }
            int q = 100;
            List<String> queries = deterministicQueries(count, q);
            long scanQStart = System.nanoTime();
            for (String query : queries) {
                blackhole += scanFor(file, query) == null ? 0 : 1;
            }
            long scanQNanos = System.nanoTime() - scanQStart;
            long mapQStart = System.nanoTime();
            for (String query : queries) {
                blackhole += map.get(query) == null ? 0 : 1;
            }
            long mapQNanos = System.nanoTime() - mapQStart;
            System.out.printf("%d,%.3f,%.3f,%.3f,%d,%.3f,%.6f,%d%n", count,
                    scan.millis(), buildNanos / 1_000_000.0, lookupNanos / 1_000.0, q,
                    scanQNanos / 1_000_000.0, mapQNanos / 1_000_000.0, map.size());
        }
        System.out.println("blackhole=" + blackhole);
    }

    private static long countLinesUnbuffered(Path file) throws IOException {
        long lines = 0;
        int value;
        int last = -1;
        boolean any = false;
        try (InputStream input = new FileInputStream(file.toFile())) {
            while ((value = input.read()) != -1) {
                any = true;
                last = value;
                if (value == '\n') lines++;
            }
        }
        return any && last != '\n' ? lines + 1 : lines;
    }

    private static long countLinesBuffered(Path file) throws IOException {
        long lines = 0;
        int value;
        int last = -1;
        boolean any = false;
        try (InputStream input = new BufferedInputStream(new FileInputStream(file.toFile()), 65536)) {
            while ((value = input.read()) != -1) {
                any = true;
                last = value;
                if (value == '\n') lines++;
            }
        }
        return any && last != '\n' ? lines + 1 : lines;
    }

    private static void createExactSizeLog(Path file, int bytes) throws IOException {
        if (Files.exists(file) && Files.size(file) == bytes) return;
        byte[] pattern = "192511019,PRESENT,2026-11-18,08:30:00\n".getBytes(StandardCharsets.UTF_8);
        try (var out = Files.newOutputStream(file, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            int written = 0;
            while (written < bytes) {
                int amount = Math.min(pattern.length, bytes - written);
                out.write(pattern, 0, amount);
                written += amount;
            }
        }
    }

    private static void createPasses(Path file, int count) throws IOException {
        try (var writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            for (int i = 0; i < count; i++) {
                writer.write(idFor(i) + "|" + String.format("%09d", 100_000_000 + i)
                        + "|Student " + i + "|R" + (i % 20) + "|Stop " + (i % 50)
                        + "|2027-05-31|1250.00");
                writer.newLine();
            }
        }
    }

    private static String idFor(int index) {
        return "P" + String.format("%06d", index);
    }

    private static String scanFor(Path file, String id) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                int delimiter = line.indexOf('|');
                if (delimiter > 0 && line.substring(0, delimiter).equals(id)) return line;
            }
        }
        return null;
    }

    private static Map<String, String> loadMap(Path file) throws IOException {
        Map<String, String> map = new HashMap<>();
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                int delimiter = line.indexOf('|');
                if (delimiter > 0) map.put(line.substring(0, delimiter), line);
            }
        }
        return map;
    }

    private static List<String> deterministicQueries(int count, int q) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < q; i++) result.add(idFor((i * 9973) % count));
        return result;
    }

    private static Measurement measure(CheckedLongSupplier operation) throws Exception {
        operation.getAsLong();
        return measureOnce(operation);
    }

    private static Measurement measureOnce(CheckedLongSupplier operation) throws Exception {
        long start = System.nanoTime();
        long result = operation.getAsLong();
        return new Measurement(System.nanoTime() - start, result);
    }

    @FunctionalInterface
    private interface CheckedLongSupplier { long getAsLong() throws Exception; }
    private record Measurement(long nanos, long result) { double millis() { return nanos / 1_000_000.0; } }
}
