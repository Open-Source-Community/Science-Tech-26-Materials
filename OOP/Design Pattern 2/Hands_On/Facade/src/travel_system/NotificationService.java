package travel_system;

public class NotificationService {
    public void sendItinerary(String customerId, String itineraryId, String details) {
        System.out.println("[Notify] Sent itinerary " + itineraryId + " to " + customerId + " details: " + details);
    }
    public void sendFailureNotice(String customerId, String reason) {
        System.out.println("[Notify] Sent failure notice to " + customerId + " reason: " + reason);
    }
}
