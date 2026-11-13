import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ContractInvestigation {
    private static final class BrokenStudent {
        final String regNo;
        BrokenStudent(String regNo) { this.regNo = regNo; }
        @Override public boolean equals(Object o) {
            return o instanceof BrokenStudent && regNo.equals(((BrokenStudent) o).regNo);
        }
    }

    private static final class MutableStudent {
        String regNo;
        MutableStudent(String regNo) { this.regNo = regNo; }
        @Override public boolean equals(Object o) {
            return o instanceof MutableStudent && regNo.equals(((MutableStudent) o).regNo);
        }
        @Override public int hashCode() { return Objects.hash(regNo); }
    }

    private static final class CorrectStudent {
        final String regNo;
        CorrectStudent(String regNo) { this.regNo = regNo; }
        @Override public boolean equals(Object o) {
            return o instanceof CorrectStudent && regNo.equals(((CorrectStudent) o).regNo);
        }
        @Override public int hashCode() { return Objects.hash(regNo); }
    }

    public static void main(String[] args) {
        BrokenStudent sameReference = new BrokenStudent("A1");
        Set<BrokenStudent> appearsCorrect = new HashSet<>();
        appearsCorrect.add(sameReference); appearsCorrect.add(sameReference);
        System.out.println("Test1 predicted=1 actual=" + appearsCorrect.size());

        Set<BrokenStudent> exposesDuplicate = new HashSet<>();
        exposesDuplicate.add(new BrokenStudent("A1")); exposesDuplicate.add(new BrokenStudent("A1"));
        System.out.println("Test2 predicted=2 actual=" + exposesDuplicate.size());

        MutableStudent mutable = new MutableStudent("A1");
        Set<MutableStudent> changedKey = new HashSet<>();
        changedKey.add(mutable); mutable.regNo = "A2";
        System.out.println("Test3 predicted_contains=false actual=" + changedKey.contains(mutable));

        Set<CorrectStudent> corrected = new HashSet<>();
        corrected.add(new CorrectStudent("A1")); corrected.add(new CorrectStudent("A1"));
        System.out.println("Corrected predicted=1 actual=" + corrected.size());
    }
}
