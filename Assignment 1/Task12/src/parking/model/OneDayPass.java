package parking.model;

import java.time.LocalDate;
import java.util.Objects;

public final class OneDayPass implements Pass {
    private final LocalDate validDate;

    public OneDayPass(LocalDate validDate) {
        this.validDate = Objects.requireNonNull(validDate);
    }

    @Override
    public boolean isValidOn(LocalDate date) {
        return validDate.equals(date);
    }
}
