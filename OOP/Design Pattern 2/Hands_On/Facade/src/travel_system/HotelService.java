package travel_system;

public class HotelService {
    public String bookHotel(String customerId, String city, String hotelName, String from, String to) {
        System.out.println("[Hotel] Booking hotel " + hotelName + " in " + city + " for " + customerId);
        // simulate failure for special sentinel city "FAIL_HOTEL"
        if ("FAIL_HOTEL".equalsIgnoreCase(city)) {
            System.out.println("[Hotel] Simulated failure booking hotel in " + city);
            return null;
        }
        return "HT-" + System.currentTimeMillis();
    }
    public void cancelHotel(String bookingId) {
        System.out.println("[Hotel] Cancelled hotel " + bookingId);
    }
}
