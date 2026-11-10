package parking.model;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Objects;

public final class MonthlyPass implements Pass {
    private final YearMonth validMonth;

    public MonthlyPass(YearMonth validMonth) {
        this.validMonth = Objects.requireNonNull(validMonth);
    }

    @Override
    public boolean isValidOn(LocalDate date) {
        return validMonth.equals(YearMonth.from(date));
    }
}
