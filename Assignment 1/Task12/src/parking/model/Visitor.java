package parking.model;

public final class Visitor extends User {
    public Visitor(String id) {
        this(id, null);
    }

    public Visitor(String id, Pass pass) {
        super(id, pass);
    }
}
