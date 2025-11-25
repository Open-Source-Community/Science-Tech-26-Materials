public class NotificationManager {

    private UserRepository userRepository;
    private EmailService emailService;
    private SlackService slackService;

    public NotificationManager(UserRepository userRepository,
                               EmailService emailService,
                               SlackService slackService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.slackService = slackService;
    }

    public void process(Notification notification) {

        // 1. Validation spaghetti
        if (notification.getType().equals("ALERT")) {
            if (notification.getPriority() == null) {
                throw new IllegalArgumentException("Alerts require priority");
            }
        } else if (notification.getType().equals("PROMOTION")) {
            if (notification.getExpiryDate() == null) {
                throw new IllegalArgumentException("Promotions require expiry date");
            }

            // GDPR logic mixed with validation
            if (userRepository.getLocation(notification.getUserId()).equals("EU")) {
                System.out.println("Filtered PROMOTION for EU user");
                return;
            }
        }

        // 2. Delivery logic mixed with core logic
        if (notification.getPreferredChannel().equals("EMAIL")) {
            emailService.send(notification.getTarget(), notification.renderForEmail());
        } else if (notification.getPreferredChannel().equals("SLACK")) {
            slackService.send(notification.getTarget(), notification.renderForSlack());
        }
    }
}
