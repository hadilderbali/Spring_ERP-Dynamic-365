package com.example.businesscentral.Controller;

import com.example.businesscentral.Entity.*;
import com.example.businesscentral.Service.EventService;
import com.example.businesscentral.Service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/notifications")
@CrossOrigin("http://localhost:4200")
public class NotificationController {

    private final NotificationService notificationService;
    private final EventService eventService;

    @PostMapping
    public ResponseEntity<Notification> sendNotification(
            @RequestBody NotificationRequest request
    ) {
        // Fetch the event by name
        Event event = eventService.findEventByName(request.getEventName());

        // Fetch roles based on role names
        Set<Role> selectedRoles = eventService.findRolesByNames(request.getRoleNames());

        Notification notification = notificationService.sendNotificationForEventAndRole(
                event,
                selectedRoles,
                request.getDescription(),
                request.getCategory(),
                request.isEnabled()
        );

        return ResponseEntity.ok(notification);
    }



    @GetMapping("/drafts")
    public ResponseEntity<Iterable<Notification>> getDraftNotifications() {
        return ResponseEntity.ok(notificationService.getDraftNotifications());
    }

    @GetMapping("/sent")
    public ResponseEntity<Iterable<Notification>> getSentNotifications() {
        return ResponseEntity.ok(notificationService.getSentNotifications());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<Iterable<Notification>> getNotificationsByCategory(
            @PathVariable Category category
    ) {
        return ResponseEntity.ok(notificationService.getNotificationsByCategory(category));
    }

    @PutMapping("/enable/{id}")
    public ResponseEntity<Map<String, String>> enableNotification(@PathVariable("id") Long id) {
        boolean result = notificationService.enableNotification(id);

        Map<String, String> response = new HashMap<>();
        if (result) {
            response.put("message", "Notification enabled and emails sent successfully.");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Notification not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

}
