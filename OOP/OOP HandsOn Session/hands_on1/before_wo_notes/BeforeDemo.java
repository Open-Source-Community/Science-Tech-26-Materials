package org.gs.hands_on1.before_wo_notes;

import java.util.List;

public class BeforeDemo {
    public static void main(String[] args) {
        NotificationManager manager = new NotificationManager();
        manager.notifyUser(1L, "Your order shipped!", "EMAIL", true);
        System.out.println();

        manager.notifyUser(2L, "Payment received!", "SMS", false);

        System.out.println();
        manager.sendBulkNotification(List.of(3L , 4L) , "Hello" , "EMAIL" , true);
    }
}