package com.thoughtsquare.service;

import com.thoughtsquare.domain.LocationEvent;

import java.util.List;

public class EventNotifyTask implements Runnable {
    private NotificationService notificationService;
    private EventService eventService;

    public EventNotifyTask(NotificationService notificationService, EventService eventService) {
        this.notificationService = notificationService;
        this.eventService = eventService;
    }

    public void run() {
        List<LocationEvent> events = eventService.getEvents();

        for (LocationEvent event : events) {
            notificationService.sendNotification(event);
        }

    }
}