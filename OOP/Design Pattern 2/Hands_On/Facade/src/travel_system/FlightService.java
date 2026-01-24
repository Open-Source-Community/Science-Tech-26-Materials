package travel_system;

public class FlightService {
    public String bookFlight(String customerId, String from, String to, String depart, String ret) {
        System.out.println("[Flight] Booking flight " + from + "->" + to + " for " + customerId);
        return "FL-" + System.currentTimeMillis();
    }
    public void cancelFlight(String bookingId) {
        System.out.println("[Flight] Cancelled flight " + bookingId);
    }
}
