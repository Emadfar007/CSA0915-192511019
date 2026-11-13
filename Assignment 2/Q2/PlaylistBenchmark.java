import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

public class PlaylistBenchmark {
    private static volatile long sink;

    private static List<Integer> filled(Supplier<List<Integer>> supplier, int size) {
        List<Integer> list = supplier.get();
        for (int i = 0; i < size; i++) list.add(i);
        return list;
    }

    private static long time(Runnable action, int repeats) {
        for (int i = 0; i < 2; i++) action.run();
        long total = 0;
        for (int i = 0; i < repeats; i++) {
            long start = System.nanoTime();
            action.run();
            total += System.nanoTime() - start;
        }
        return total / repeats;
    }

    private static void runFor(String type, Supplier<List<Integer>> supplier, int size) {
        long addEnd = time(() -> {
            List<Integer> list = supplier.get();
            for (int i = 0; i < size; i++) list.add(i);
            sink += list.size();
        }, 5) / size;
        List<Integer> list = filled(supplier, size);
        int churn = 100;
        long front = time(() -> {
            for (int i = 0; i < churn; i++) { list.add(0, -i); sink += list.remove(0); }
        }, 5) / churn;
        long middle = time(() -> {
            int middleIndex = list.size() / 2;
            for (int i = 0; i < churn; i++) { list.add(middleIndex, -i); sink += list.remove(middleIndex); }
        }, 5) / churn;
        int gets = 1000;
        long get = time(() -> {
            for (int i = 0; i < gets; i++) sink += list.get(list.size() / 2);
        }, 5) / gets;
        long iterate = time(() -> {
            long total = 0;
            for (int value : list) total += value;
            sink += total;
        }, 10) / size;
        System.out.printf(Locale.ROOT, "%d,%s,%d,%d,%d,%d,%d%n",
                size, type, addEnd, front, middle, get, iterate);
    }

    private static void dequeChangedCondition(int size) {
        Deque<Integer> deque = new ArrayDeque<>();
        for (int i = 0; i < size; i++) deque.addLast(i);
        int operations = 10_000;
        long start = System.nanoTime();
        for (int i = 0; i < operations; i++) {
            deque.addFirst(-i); deque.addLast(i); sink += deque.removeFirst(); sink += deque.removeLast();
        }
        System.out.printf(Locale.ROOT, "%d,ArrayDequeUpNext,%d%n", size,
                (System.nanoTime() - start) / (operations * 4));
    }

    public static void main(String[] args) {
        System.out.println("size,list,add_end_ns,front_churn_ns,middle_churn_ns,get_middle_ns,iterate_per_element_ns");
        for (int size : new int[]{1_000, 10_000, 50_000}) {
            runFor("ArrayList", ArrayList::new, size);
            runFor("LinkedList", LinkedList::new, size);
        }
        System.out.println("size,changed_condition,avg_ns_per_end_operation");
        for (int size : new int[]{1_000, 10_000, 50_000}) dequeChangedCondition(size);
        System.out.println("sink=" + sink);
    }
}
