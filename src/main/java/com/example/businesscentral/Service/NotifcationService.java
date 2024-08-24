package com.example.businesscentral.Service;

import com.example.businesscentral.Entity.Category;
import com.example.businesscentral.Entity.Notification;
import com.example.businesscentral.Entity.User;
import com.example.businesscentral.Repository.NotificationRepository;
import com.example.businesscentral.Repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class NotifcationService {
    final NotificationRepository notificationRepository;
    final  EmailService emailService;
    private final JavaMailSender javaMailSender; // Ensure this dependency is injected
    private final UserRepository userRepository;}

 /**   public Notification createNotification(Notification notification, List<Long> userIds) {
        // Save the notification to the database
        Notification savedNotification = notificationRepository.save(notification);

        // Check if the notification is enabled and if there are user IDs provided
        if (savedNotification.isEnabled() && userIds != null && !userIds.isEmpty()) {
            // Retrieve the list of users based on the provided user IDs
            List<User> recipients = userRepository.findAllById(userIds);

            // Send notification email to each recipient
            for (User recipient : recipients) {
                sendNotificationEmail(recipient, savedNotification);
            }
        }

        // Return the saved notification
        return savedNotification;
    }

 /**   public List<User> getUsersBasedOnSelections(EventType eventType, Map<String, List<Long>> selections) {
        switch (eventType) {
            case TICKET_CREATED:
                return userRepository.findAll(); // Or use specific criteria if needed
            case TICKET_ASSIGNED:
                return userRepository.findUsersByTicketIds(selections.get("tickets"));
            case PROJECT_ASSIGNED:
                return userRepository.findUsersByProjectIds(selections.get("projects"));
            case USER_ASSIGNED_TO_TEAM:
                return userRepository.findUsersByTeamIds(selections.get("teams"));
            default:
                return List.of(); // No recipients for unknown event types
        }
    }

    private void sendNotificationEmail(User recipient, Notification notification) {
        String subject = getEmailSubject(notification.getEventType());
        String htmlContent = getEmailContent(notification, recipient);

        emailService.sendHtmlEmail(recipient.getUsermail(), subject, htmlContent);
    }

    private String getEmailSubject(EventType eventType) {
        switch (eventType) {
            case TICKET_CREATED:
                return "New Ticket Created";
            case TICKET_ASSIGNED:
                return "Ticket Assigned";
            case TICKET_COMPLETED:
                return "Ticket Completed";
            case PROJECT_ASSIGNED:
                return "Project Assigned";
            case USER_ASSIGNED_TO_TEAM:
                return "User Assigned to Team";
            default:
                return "Notification";
        }
    }

    private String getEmailContent(Notification notification, User recipient) {
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
                "<h1>" + getEmailSubject(notification.getEventType()) + "</h1>" +
                "</div>" +
                "<div class='content'>" +
                "<h2>Hello " + recipient.getUsername() + ",</h2>" +
                "<p>You have a new notification regarding a <span class='category'>" + notification.getCategory() + "</span> event:</p>" +
                "<div class='ticket-info'>" +
                "<p><strong>Event Type:</strong> " + notification.getEventType() + "</p>" +
                "<p><strong>Description:</strong> " + notification.getDescription() + "</p>" +
                "<p><strong>Date:</strong> " + notification.getDate() + "</p>" + // Assuming there's a createdAt field
                "</div>" +
                "<p>If you have any questions or need further assistance, please do not hesitate to contact our support team.</p>" +
                "<p>Best regards,<br>AIM-IT Consult Team</p>" +
                "<a href='http://AIM-IT Consult.com' class='button'>View Details</a>" + // Example button
                "</div>" +
                "<div class='footer'>" +
                "<p>&copy; 2024 AIM-IT CONSULT. All rights reserved.</p>" +
                "<p>Ariena,Menzah 7</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
    public Notification enableNotification(Long id, List<Long> userIds) {
        Optional<Notification> optionalNotification = notificationRepository.findById(id);
        if (optionalNotification.isPresent()) {
            Notification notification = optionalNotification.get();
            notification.setEnabled(true);
            Notification savedNotification = notificationRepository.save(notification);

            if (userIds != null && !userIds.isEmpty()) {
                List<User> recipients = userRepository.findAllById(userIds);
                for (User recipient : recipients) {
                    sendNotificationEmail(recipient, savedNotification);
                }
            }

            return savedNotification;
        } else {
            throw new ResourceNotFoundException("Notification not found with id " + id);
        }
    }


    public List<Notification> getDraftNotifications() {
        return notificationRepository.findByEnabled(false);
    }

    public List<Notification> getSentNotifications() {
        return notificationRepository.findByEnabled(true);
    }
    public List<Notification> getNotificationsByCategory(Category category) {
        return notificationRepository.findByCategory(category);
    }

}**/

