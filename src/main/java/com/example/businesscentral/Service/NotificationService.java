package com.example.businesscentral.Service;

import com.example.businesscentral.Entity.*;
import com.example.businesscentral.Repository.NotificationRepository;
import com.example.businesscentral.Repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;

    public Notification sendNotificationForEventAndRole(
            Event event, Set<Role> selectedRoles, String description, Category category, boolean isEnabled) {

        // Create a notification based on the provided description and category
        Notification notification = new Notification();
        notification.setDescription(description);
        notification.setDate(LocalDate.now());
        notification.setNotification_read(false);
        notification.setEnabled(isEnabled);
        notification.setCategory(category);
        notification.setEvent(event);

        // Save the notification initially
        Notification savedNotification = notificationRepository.save(notification);

        // Fetch users based on roles and event
        List<User> usersToNotify = userRepository.findByRolesAndEvent(selectedRoles, event);

        // Associate these users with the notification
        savedNotification.setUsers(new HashSet<>(usersToNotify));

        // Save the notification again to update the users
        Notification finalNotification = notificationRepository.save(savedNotification);

        // Send notifications to the users only if the notification is enabled
        if (isEnabled) {
            usersToNotify.forEach(user -> sendNotificationEmail(user, finalNotification));
        }

        return finalNotification;
    }


    private void sendNotificationEmail(User recipient, Notification notification) {
        String subject = getEmailSubject(notification.getCategory());
        String htmlContent = getEmailContent(notification, recipient);

        emailService.sendHtmlEmail(recipient.getUsermail(), subject, htmlContent);
    }

    private String getEmailSubject(Category category) {
        switch (category) {
            case INFORMATIONAL:
                return "Information Notification";
            case WARNING:
                return "Warning Notification";
            case ERROR:
                return "Error Notification";
            default:
                return "Notification";
        }
    }

    private String getEmailContent(Notification notification, User recipient) {
        String fromEmail = "artofcode0@gmail.com"; // Consider making this configurable

        return "<!DOCTYPE html>" +
                "<html lang='en'>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<style>" +
                "body { font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }" +
                ".container { max-width: 600px; margin: 20px auto; background-color: #ffffff; padding: 0; border-radius: 8px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); overflow: hidden; }" +
                ".header { background-color: #0073e6; color: #ffffff; padding: 20px; text-align: center; }" +
                ".header h1 { margin: 0; font-size: 28px; font-weight: bold; }" +
                ".content { padding: 20px; }" +
                ".content h2 { font-size: 22px; color: #333333; margin-top: 0; }" +
                ".content p { margin: 0 0 10px; line-height: 1.6; color: #555555; }" +
                ".ticket-info { background-color: #f9f9f9; padding: 15px; border: 1px solid #e0e0e0; border-radius: 6px; margin-bottom: 20px; }" +
                ".ticket-info p { margin: 5px 0; }" +
                ".category { font-size: 16px; color: #0073e6; font-weight: bold; margin: 10px 0; }" +
                ".footer { text-align: center; padding: 20px; font-size: 12px; color: #777777; background-color: #f4f4f4; }" +
                ".button { display: inline-block; padding: 10px 20px; font-size: 16px; color: #ffffff; background-color: #0073e6; border-radius: 5px; text-decoration: none; }" +
                ".button:hover { background-color: #005bb5; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<h1>" + getEmailSubject(notification.getCategory()) + "</h1>" +
                "</div>" +
                "<div class='content'>" +
                "<h2>Hello " + recipient.getUsername() + ",</h2>" +
                "<p>You have a new notification:</p>" +
                "<div class='ticket-info'>" +
                "<p><strong>Description:</strong> " + notification.getDescription() + "</p>" +
                "<p><strong>Date:</strong> " + notification.getDate() + "</p>" +
                "</div>" +
                "<p>From: " + fromEmail + "</p>" +
                "<p>To: " + recipient.getUsermail() + "</p>" +
                "<p>If you have any questions or need further assistance, please do not hesitate to contact our support team.</p>" +
                "<p>Best regards,<br>AIM-IT Consult Team</p>" +
                "<a href='http://AIM-ITConsult.com' class='button'>View Details</a>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>&copy; 2024 AIM-IT CONSULT. All rights reserved.</p>" +
                "<p>Ariena, Menzah 7</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
    public List<Notification> getDraftNotifications() {
        return notificationRepository.getDraftNotifications();
    }

    public List<Notification> getSentNotifications() {
        return notificationRepository.getSentNotifications();
    }
    public List<Notification> getNotificationsByCategory(Category category) {
        return notificationRepository.findByCategory(category);
    }

    public boolean enableNotification(Long id) {
        Optional<Notification> optionalNotification = notificationRepository.findById(id);
        if (optionalNotification.isPresent()) {
            Notification notification = optionalNotification.get();
            notification.setEnabled(true);
            notificationRepository.save(notification);

            // Fetch users associated with the notification
            Set<User> usersToNotify = notification.getUsers();

            // Send email notifications to all users associated with the notification
            if (usersToNotify != null) {
                for (User user : usersToNotify) {
                    sendNotificationEmail(user, notification);
                }
            }
            return true;
        }
        return false;
    }


}
