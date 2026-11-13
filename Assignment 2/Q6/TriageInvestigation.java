import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.PriorityQueue;
import java.util.Random;

public class TriageInvestigation {
    private record P(String name, int severity, long arrival) { }
    private static final Comparator<P> ORDER = Comparator.comparingInt(P::severity)
            .thenComparingLong(P::arrival);
    private static volatile long sink;

    private static void correctness() {
        PriorityQueue<P> q = new PriorityQueue<>(ORDER);
        int[] severities = {3, 1, 4, 1, 5, 2, 3, 2};
        for (int i = 0; i < severities.length; i++) q.add(new P("P" + i, severities[i], i));
        List<String> actual = new ArrayList<>();
        while (!q.isEmpty()) actual.add(q.remove().name());
        System.out.println("hand_order=P1 P3 P5 P7 P0 P6 P2 P4");
        System.out.println("actual_order=" + String.join(" ", actual));
    }

    private static void growth(int size) {
        long admit = 0, serve = 0;
        for (int run = 0; run < 5; run++) {
            PriorityQueue<P> q = new PriorityQueue<>(ORDER);
            Random random = new Random(100 + run);
            long start = System.nanoTime();
            for (int i = 0; i < size; i++) q.add(new P("P" + i, 1 + random.nextInt(5), i));
            admit += System.nanoTime() - start;
            start = System.nanoTime();
            while (!q.isEmpty()) sink += q.remove().arrival();
            serve += System.nanoTime() - start;
        }
        System.out.printf(Locale.ROOT, "%d,%d,%d%n", size, admit / 5 / size, serve / 5 / size);
    }

    public static void main(String[] args) {
        correctness();
        System.out.println("size,admit_avg_ns,serve_avg_ns");
        for (int size : new int[]{1_000, 5_000, 20_000}) growth(size);
        System.out.println("fair_rule=effective severity improves by one after each 30 waiting minutes; rebuild before selection");
        System.out.println("sink=" + sink);
    }
}
