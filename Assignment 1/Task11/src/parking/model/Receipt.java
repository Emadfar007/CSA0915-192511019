package parking.model;

public record Receipt(long ticketNumber, String registrationNumber,
                      int minutes, double amount) {
}
