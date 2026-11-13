package parking.model;

public abstract class User {
    private final String id;
    private final boolean validMonthlyPass;

    protected User(String id, boolean validMonthlyPass) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("user id is required");
        }
        this.id = id;
        this.validMonthlyPass = validMonthlyPass;
    }

    public final String getId() {
        return id;
    }

    public final boolean hasValidMonthlyPass() {
        return validMonthlyPass;
    }
}
