public record EventRecord(int id, String name, int capacity) {
    @Override public String toString() { return name; }
}
