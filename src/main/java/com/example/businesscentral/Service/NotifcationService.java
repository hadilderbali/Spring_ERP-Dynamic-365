package com.example.businesscentral.Service;

import com.example.businesscentral.Entity.EventType;
import com.example.businesscentral.Entity.Notification;
import com.example.businesscentral.Repository.NotificationRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class NotifcationService {
    final NotificationRepository notificationRepository;
    final  EmailService emailService;
    private final JavaMailSender javaMailSender; // Ensure this dependency is injected

    public Notification createNotif(Notification notification) {
        Notification savedNotification = notificationRepository.save(notification);

        if (savedNotification.isEnabled()) {
            switch (notification.getEventType()) {
                case TICKET_CREATED:
                    sendTicketCreatedNotificationEmail(savedNotification);
                    break;
                case TICKET_ASSIGNED:
                    sendTicketAssignedNotificationEmail(savedNotification);
                case TICKET_COMPLETED:



            }
        }
    }

    private void sendTicketCreatedNotificationEmail(Notification notification) {
        String recipientEmail = "recipient@example.com";
        String subject = "New Ticket Created";
        String htmlContent = "<h1>New Ticket Created</h1>" +
                "<p>A new ticket has been created: " + notification.getDescription() + "</p>";

        sendEmail(recipientEmail, subject, htmlContent);
    }

    private void sendTicketAssignedNotificationEmail(Notification notification) {
        String recipientEmail = "recipient@example.com";
        String subject = "Ticket Assigned";
        String htmlContent = "<h1>Ticket Assigned</h1>" +
                "<p>A ticket has been assigned: " + notification.getDescription() + "</p>";

        sendEmail(recipientEmail, subject, htmlContent);
    }

    private void sendTicketCompletedNotificationEmail(Notification notification) {
        String recipientEmail = "recipient@example.com";
        String subject = "Ticket Completed";
        String htmlContent = "<h1>Ticket Completed</h1>" +
                "<p>A ticket has been completed: " + notification.getDescription() + "</p>";

        sendEmail(recipientEmail, subject, htmlContent);
    }

    private void sendEmail(String recipientEmail, String subject, String htmlContent) {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
        try {
            helper.setFrom("sender@example.com");
            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


}
