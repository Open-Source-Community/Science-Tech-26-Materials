package travel_system;

public class TripDetails {
    public final String from;
    public final String to;
    public final String depart;
    public final String ret;
    public final String hotelName;
    public final String carType;
    public final double estimatedPrice;

    public TripDetails(String from, String to, String depart, String ret,
                       String hotelName, String carType, double estimatedPrice) {
        this.from = from; this.to = to; this.depart = depart; this.ret = ret;
        this.hotelName = hotelName; this.carType = carType; this.estimatedPrice = estimatedPrice;
    }
}
