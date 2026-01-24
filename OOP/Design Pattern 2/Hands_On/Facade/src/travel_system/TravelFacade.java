package travel_system;

public class TravelFacade {
    private final FlightService flight;
    private final HotelService hotel;
    private final CarService car;
    private final PaymentService payment;
    private final NotificationService notifier;

    public TravelFacade(FlightService flight, HotelService hotel, CarService car,
                        PaymentService payment, NotificationService notifier) {
        this.flight = flight; this.hotel = hotel; this.car = car;
        this.payment = payment; this.notifier = notifier;
    }

    public TripResult bookTrip(String customerId, TripDetails trip) {
        System.out.println("[Facade] Booking trip for " + customerId + ": " + trip);

        // 1) Validate minimal info
        if (trip.estimatedPrice <= 0) {
            return new TripResult(false, null, "invalid price");
        }

        String paymentTx = null;
        String flightId = null;
        String hotelId = null;
        String carId = null;

        // 2) Charge customer
        try {
            paymentTx = payment.charge(customerId, trip.estimatedPrice);
            if (paymentTx == null) {
                return new TripResult(false, null, "payment_failed");
            }
        } catch (Exception e) {
            return new TripResult(false, null, "payment_exception: " + e.getMessage());
        }

        // 3) Book flight
        try {
            flightId = flight.bookFlight(customerId, trip.from, trip.to, trip.depart, trip.ret);
            if (flightId == null) {
                payment.refund(paymentTx);
                return new TripResult(false, null, "flight_booking_failed");
            }
        } catch (Exception e) {
            payment.refund(paymentTx);
            return new TripResult(false, null, "flight_exception: " + e.getMessage());
        }

        // 4) Book hotel
        try {
            hotelId = hotel.bookHotel(customerId, trip.to, trip.hotelName, trip.depart, trip.ret);
            if (hotelId == null) {
                // compensate
                flight.cancelFlight(flightId);
                payment.refund(paymentTx);
                return new TripResult(false, null, "hotel_booking_failed");
            }
        } catch (Exception e) {
            // compensate
            flight.cancelFlight(flightId);
            payment.refund(paymentTx);
            return new TripResult(false, null, "hotel_exception: " + e.getMessage());
        }

        // 5) Book car (optional)
        try {
            carId = car.bookCar(customerId, trip.to, trip.carType, trip.depart, trip.ret);
            if (carId == null) {
                // compensate previous bookings
                hotel.cancelHotel(hotelId);
                flight.cancelFlight(flightId);
                payment.refund(paymentTx);
                return new TripResult(false, null, "car_booking_failed");
            }
        } catch (Exception e) {
            // compensate
            hotel.cancelHotel(hotelId);
            flight.cancelFlight(flightId);
            payment.refund(paymentTx);
            return new TripResult(false, null, "car_exception: " + e.getMessage());
        }

        // 6) All good: create itinerary id and notify customer
        String itineraryId = "ITIN-" + System.currentTimeMillis();
        notifier.sendItinerary(customerId, itineraryId,
                String.format("flight:%s hotel:%s car:%s", flightId, hotelId, carId));

        return new TripResult(true, itineraryId, "booked");
    }
}
