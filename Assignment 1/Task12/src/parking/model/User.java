package parking.model;

import java.time.LocalDate;

public abstract class User {
    private final String id;
    private final Pass pass;

    protected User(String id, Pass pass) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("id is required");
        this.id = id;
        this.pass = pass;
    }

    public final String getId() { return id; }

    public final boolean hasValidPass(LocalDate date) {
        return pass != null && pass.isValidOn(date);
    }
}
