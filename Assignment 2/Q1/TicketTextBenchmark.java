import java.util.Locale;
import java.util.function.Supplier;

public class TicketTextBenchmark {
    private static volatile int sink;

    private record Workload(String name, int tickets, int fields) { }
    private record Measurement(long averageNanos, long averageMemoryBytes, long estimatedObjects) { }

    private static String withString(int fields) {
        String ticket = "";
        for (int i = 0; i < fields; i++) ticket = ticket + "F" + i + "=VALUE" + i + ";";
        return ticket;
    }

    private static String withBuilder(int fields) {
        StringBuilder ticket = new StringBuilder(fields * 12);
        for (int i = 0; i < fields; i++) ticket.append("F").append(i).append("=VALUE").append(i).append(';');
        return ticket.toString();
    }

    private static String withBuffer(int fields) {
        StringBuffer ticket = new StringBuffer(fields * 12);
        for (int i = 0; i < fields; i++) ticket.append("F").append(i).append("=VALUE").append(i).append(';');
        return ticket.toString();
    }

    private static long usedMemory() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    private static Measurement measure(Workload workload, Supplier<String> factory,
                                       long objectsPerTicket) {
        for (int warm = 0; warm < 2; warm++) {
            for (int i = 0; i < workload.tickets; i++) sink ^= factory.get().length();
        }
        long nanos = 0;
        long memory = 0;
        final int repetitions = 5;
        for (int run = 0; run < repetitions; run++) {
            System.gc();
            long beforeMemory = usedMemory();
            long start = System.nanoTime();
            String[] retained = new String[workload.tickets];
            for (int i = 0; i < retained.length; i++) retained[i] = factory.get();
            nanos += System.nanoTime() - start;
            memory += Math.max(0, usedMemory() - beforeMemory);
            sink ^= retained[retained.length - 1].length();
        }
        return new Measurement(nanos / repetitions, memory / repetitions,
                objectsPerTicket * workload.tickets);
    }

    private static void print(String workload, String method, Measurement value) {
        System.out.printf(Locale.ROOT, "%s,%s,%d,%d,%d%n", workload, method,
                value.averageNanos, value.averageMemoryBytes, value.estimatedObjects);
    }

    public static void main(String[] args) {
        Workload[] workloads = {
                new Workload("short_1x10", 1, 10),
                new Workload("server_1000x50", 1000, 50),
                new Workload("wide_200x200", 200, 200)
        };
        System.out.println("workload,method,avg_nanos,avg_memory_bytes,estimated_intermediate_objects");
        for (Workload w : workloads) {
            print(w.name, "StringPlus", measure(w, () -> withString(w.fields), 3L * w.fields));
            print(w.name, "StringBuilder", measure(w, () -> withBuilder(w.fields), 2));
            print(w.name, "StringBuffer", measure(w, () -> withBuffer(w.fields), 2));
        }
        System.out.println("sink=" + sink);
    }
}
