package travel_system;

public class TripResult {
    public final boolean success;
    public final String itineraryId; // null on failure
    public final String message;

    public TripResult(boolean success, String itineraryId, String message) {
        this.success = success; this.itineraryId = itineraryId; this.message = message;
    }

    @Override
    public String toString() {
        return "TripResult{success=" + success + ", itineraryId=" + itineraryId + ", message='" + message + "'}";
    }
}
