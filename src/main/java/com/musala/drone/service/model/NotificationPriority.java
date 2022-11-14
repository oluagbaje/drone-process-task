/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.musala.drone.service.model;

/**
 *
 * @author ADMIN
 */
public enum NotificationPriority {
    HIGH(3),
    NORMAL(2),
    LOW(1);

    private int priority;

    public int getPriority() {
        return priority;
    }

    private NotificationPriority(int priority) {
        this.priority = priority;
    }

    public static NotificationPriority getNotificationPriority(int priority) {
        for (NotificationPriority notifPriory : NotificationPriority.values()) {
            if (notifPriory.priority == priority) {
                return notifPriory;
            }
        }
        return null;
    }

}
