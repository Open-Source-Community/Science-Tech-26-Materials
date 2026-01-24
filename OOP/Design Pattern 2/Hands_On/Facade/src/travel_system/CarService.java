package travel_system;

public class CarService {
    public String bookCar(String customerId, String city, String carType, String from, String to) {
        System.out.println("[Car] Booking " + carType + " in " + city + " for " + customerId);
        return "CR-" + System.currentTimeMillis();
    }
    public void cancelCar(String bookingId) {
        System.out.println("[Car] Cancelled car " + bookingId);
    }
}
