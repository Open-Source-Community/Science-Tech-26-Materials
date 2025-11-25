import java.time.LocalDate;

public class Notification {
    private String id;
    private String userId;
    private String target;
    private String type;               // ALERT or PROMOTION
    private Integer priority;          // Only for ALERT
    private LocalDate expiryDate;      // Only for PROMOTION
    private String preferredChannel;   // EMAIL or SLACK

    public Notification(String id, String userId, String target,
                        String type, Integer priority,
                        LocalDate expiryDate, String preferredChannel) {
        this.id = id;
        this.userId = userId;
        this.target = target;
        this.type = type;
        this.priority = priority;
        this.expiryDate = expiryDate;
        this.preferredChannel = preferredChannel;
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getTarget() { return target; }
    public String getType() { return type; }
    public Integer getPriority() { return priority; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public String getPreferredChannel() { return preferredChannel; }

    // Dummy rendering methods
    public String renderForEmail() {
        return "[EMAIL] Notification " + id;
    }

    public String renderForSlack() {
        return "[SLACK] Notification " + id;
    }
}
