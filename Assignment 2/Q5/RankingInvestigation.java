import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RankingInvestigation {
    private record Ranked(String name, int points, int goalDiff) { }

    public static void main(String[] args) {
        Comparator<Ranked> correct = Comparator.comparingInt(Ranked::points).reversed()
                .thenComparing(Comparator.comparingInt(Ranked::goalDiff).reversed())
                .thenComparing(Ranked::name);
        List<Ranked> ties = new ArrayList<>(List.of(
                new Ranked("Zulu", 10, 2), new Ranked("Alpha", 10, 2),
                new Ranked("Beta", 10, 5), new Ranked("Gamma", 8, 20)));
        ties.sort(correct);
        System.out.println("Correct ties: " + ties);

        Comparator<Ranked> faultySubtraction = (a, b) -> b.points - a.points;
        Ranked min = new Ranked("MIN", Integer.MIN_VALUE, 0);
        Ranked max = new Ranked("MAX", Integer.MAX_VALUE, 0);
        System.out.println("Faulty overflow compare(MAX,MIN)=" + faultySubtraction.compare(max, min));
        System.out.println("Correct compare(MAX,MIN)=" + correct.compare(max, min));
        System.out.println("Tie resolved alphabetically=" + (correct.compare(
                new Ranked("Alpha", 5, 1), new Ranked("Zulu", 5, 1)) < 0));
    }
}
