package com.example.businesscentral.Controller;

import com.example.businesscentral.Entity.*;
import com.example.businesscentral.Service.NotifcationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor

@RestController
@RequestMapping("/notifications")
@CrossOrigin("http://localhost:4200")

public class NotificationController {

    private final NotifcationService notifcationService;

  /**  @PostMapping
    public ResponseEntity<Notification> createNotification(
            @RequestBody NotificationRequest notificationRequest) {

        // Create and save the notification
        Notification notification = new Notification();
        notification.setDescription(notificationRequest.getDescription());
        notification.setEventType(notificationRequest.getEventType());
        notification.setEnabled(notificationRequest.isEnabled());
        notification.setDate(LocalDate.now()); // Assuming `date` is part of the request
        notification.setCategory(notificationRequest.getCategory()); // Assuming `category` is part of the request

        // Call the service method to handle user selections and sending notifications
        Notification savedNotification = notifcationService.createNotification(
                notification,
                notificationRequest.getUserIds()
        );

        return ResponseEntity.ok(savedNotification);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsersBasedOnSelections(
            @RequestParam EventType eventType,
            @RequestParam(required = false) List<Long> ticketIds,
            @RequestParam(required = false) List<Long> projectIds,
            @RequestParam(required = false) List<Long> teamIds) {

        Map<String, List<Long>> selections = new HashMap<>();
        selections.put("tickets", ticketIds != null ? ticketIds : List.of());
        selections.put("projects", projectIds != null ? projectIds : List.of());
        selections.put("teams", teamIds != null ? teamIds : List.of());

        List<User> users = notifcationService.getUsersBasedOnSelections(eventType, selections);
        return ResponseEntity.ok(users);
    }
    @PostMapping("/{idn}/enable")
    public ResponseEntity<Notification> enableNotification(@PathVariable Long idn, @RequestBody List<Long> userIds) {
        Notification enabledNotification = notifcationService.enableNotification(idn, userIds);
        return ResponseEntity.ok(enabledNotification);
    }

    @GetMapping("/drafts")
    public ResponseEntity<List<Notification>> getDraftNotifications() {
        List<Notification> drafts = notifcationService.getDraftNotifications();
        return ResponseEntity.ok(drafts);
    }

    @GetMapping("/sent")
    public ResponseEntity<List<Notification>> getSentNotifications() {
        List<Notification> sentNotifications = notifcationService.getSentNotifications();
        return ResponseEntity.ok(sentNotifications);
    }
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Notification>> getNotificationsByCategory(@PathVariable Category category) {
        List<Notification> notifications = notifcationService.getNotificationsByCategory(category);
        return ResponseEntity.ok(notifications);
    }**/

}