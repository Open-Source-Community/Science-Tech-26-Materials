import travel_system.*;


void main() {
    FlightService flight = new FlightService();
    HotelService hotel = new HotelService();
    CarService car = new CarService();
    PaymentService payment = new PaymentService();
    NotificationService notifier = new NotificationService();

    TravelFacade facade = new TravelFacade(flight, hotel, car, payment, notifier);

    TripDetails goodTrip = new TripDetails("NYC", "LON", "2026-04-10", "2026-04-20", "Grand Hotel", "Sedan", 1200.0);
    TripDetails problematicTrip = new TripDetails("NYC", "FAIL_HOTEL", "2026-04-10", "2026-04-20", "Bad Hotel", "SUV", 900.0);

    System.out.println("=== Attempting successful trip ===");
    TripResult res1 = facade.bookTrip("cust-100", goodTrip);
    System.out.println("Result: " + res1 + "\n");

    System.out.println("=== Attempting trip that will fail at hotel booking (shows rollback) ===");
    TripResult res2 = facade.bookTrip("cust-101", problematicTrip);
    System.out.println("Result: " + res2 + "\n");
}

