package org.gs.hands_on1.before_wo_notes;

import java.util.List;

//After Refactoring, add push notification method
public class NotificationManager {

    public void notifyUser(Long userId, String message, String type,
                           boolean notifyAdmin) {

        if ("EMAIL".equals(type)) {
            String userEmail = "user@gmail.com";  // assume this is fetched from a database like (select email from users where id = userId)

            //Preparing email connection
            System.out.println("Connecting to SMTP server...");
            System.out.println("EMAIL to " + userEmail);
            System.out.println(message);

            if (notifyAdmin) {
                //Note admin notifications only via email
                //we need also to notify the admin
                String adminEmail = "admin@gmail.com";
                System.out.println("EMAIL to " + adminEmail);
                System.out.println("ADMIN ALERT : User " + userId + " received " + type);
            }
        } else if ("SMS".equals(type)) {
            String userPhone = "1234567890"; // select phone from users where id = userId

            System.out.println("Connecting to SMS gateway...");
            System.out.println("SMS to " + userPhone);
            System.out.println("   Message: " + message);

            if (notifyAdmin) {
                String adminEmail = "admin@gmail.com";
                System.out.println("Connecting to SMTP server...");
                System.out.println("EMAIL to " + adminEmail);
                System.out.println("ADMIN ALERT : User " + userId + " received " + type);
            }
        }

        System.out.println("INFO: Notification sent to " + userId + " via " + type);
    }

    public void sendBulkNotification(List<Long> userIds, String message, String type, boolean notifyAdmin) {
        for (Long userId : userIds) {
            if ("EMAIL".equals(type)) {
                String userEmail = "user@gmail.com";

                System.out.println("Connecting to SMTP server...");
                System.out.println("EMAIL to " + userEmail);
                System.out.println(message);

                if (notifyAdmin) {
                    String adminEmail = "admin@gmail.com";
                    System.out.println("EMAIL to " + adminEmail);
                    System.out.println("ADMIN ALERT : User " + userId + " received " + type);
                }

            } else if ("SMS".equals(type)) {
                String userPhone = "1234567890"; // select phone from users where id = userId

                System.out.println("Connecting to SMS gateway...");
                System.out.println("SMS to " + userPhone);
                System.out.println("   Message: " + message);

                if (notifyAdmin) {
                    String adminEmail = "admin@gmail.com";
                    System.out.println("Connecting to SMTP server...");
                    System.out.println("EMAIL to " + adminEmail);
                    System.out.println("ADMIN ALERT : User " + userId + " received " + type);
                }
            }
        }
    }
}
