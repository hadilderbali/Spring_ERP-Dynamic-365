package com.example.businesscentral.Service;

import com.example.businesscentral.Entity.*;
import com.example.businesscentral.Repository.ProjectRepository;
import com.example.businesscentral.Repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor

public class TicketService {

    final TicketRepository ticketInterface;
    final ProjectRepository projectInterface;
    final  EmailService emailService;

    public Ticket createTicket(String title, String description, Long projectId) {
        Optional<Project> optionalProject = projectInterface.findById(projectId);
        if (optionalProject.isPresent()) {
            Project project = optionalProject.get();

            // Create the ticket
            Ticket ticket = new Ticket();
            ticket.setTitle(title);
            ticket.setDescription(description);
            ticket.setProject(project);
            ticket.setCreatedDate(LocalDate.now());
            ticket.setStatus(Status.NEW);

            // Save the ticket
            Ticket savedTicket = ticketInterface.save(ticket);

            // Send email notifications to all teams associated with the project
            Set<Team> teams = project.getTeams();
            for (Team team : teams) {
                String subject = "🚀 New Ticket Assigned to Your Project!";
                String htmlContent = "<!DOCTYPE html>"
                        + "<html>"
                        + "<head>"
                        + "<title>New Ticket Assigned</title>"
                        + "<style>"
                        + "body {font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #e0e0e0;}"
                        + "table {border-collapse: collapse; width: 100%;}"
                        + "h1 {color: #ffffff; margin: 0; padding: 20px; background-color: #b30000; text-align: center;}"
                        + "p {color: #333333; line-height: 1.6; margin: 20px 0;}"
                        + ".container {max-width: 600px; margin: 20px auto; background-color: #ffffff; padding: 20px; border: 2px solid #b30000; border-radius: 10px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);}"
                        + ".header {background-color: #b30000; color: #ffffff; padding: 10px 20px; border-radius: 10px 10px 0 0;}"
                        + ".footer {text-align: center; padding: 10px 20px; color: #777777;}"
                        + ".button {display: inline-block; padding: 10px 20px; margin-top: 20px; font-size: 16px; color: #ffffff; background-color: #7a7a7a; border-radius: 5px; text-align: center; text-decoration: none;}"
                        + "</style>"
                        + "</head>"
                        + "<body>"
                        + "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">"
                        + "<tr>"
                        + "<td align=\"center\">"
                        + "<!-- Email Content -->"
                        + "<table width=\"600\" cellspacing=\"0\" cellpadding=\"0\" class=\"container\">"
                        + "<tr>"
                        + "<td align=\"center\" class=\"header\">"
                        + "<!-- Header -->"
                        + "<h1>🚀 New Ticket Assigned!</h1>"
                        + "</td>"
                        + "</tr>"
                        + "<tr>"
                        + "<td align=\"left\" bgcolor=\"#ffffff\">"
                        + "<!-- Body -->"
                        + "<p>Hello,</p>"
                        + "<p>A new ticket has been assigned to your project. Please find the details below:</p>"
                        + "<p><strong>Title:</strong> " + title + "</p>"
                        + "<p><strong>Project Name:</strong> " + project.getNameP() + "</p>"
                        + "<p>Click the button below to view and manage the ticket:</p>"
                        + "<p><a href=\"#\" class=\"button\">View Ticket</a></p>"
                        + "<p>Thank you!</p>"
                        + "</td>"
                        + "</tr>"
                        + "<tr>"
                        + "<td align=\"center\" class=\"footer\">"
                        + "<!-- Footer -->"
                        + "<p>&copy; 2024 Your Company. All rights reserved.</p>"
                        + "</td>"
                        + "</tr>"
                        + "</table>"
                        + "</td>"
                        + "</tr>"
                        + "</table>"
                        + "</body>"
                        + "</html>";

                emailService.sendHtmlEmail(team.getEmail(), subject, htmlContent);


            }

            return savedTicket;
        } else {
            throw new RuntimeException("Project not found");
        }
    }


    public Ticket getTicket(Long id) {
        return ticketInterface.findById(id).orElse(null);
    }
    public List<Ticket> getAllTicket(){
        return  ticketInterface.findAll();
    }
    public Ticket updateTicket (Long id,Ticket ticket){
        Ticket ticket1 = ticketInterface.findById(id).orElse(null);
        ticket1.setTitle(ticket.getTitle());
        ticket1.setDescription(ticket.getDescription());
        ticket1.setDateF(ticket.getDateF());
        ticket1.setCreatedDate(ticket.getCreatedDate());
        ticket1.setDateS(ticket.getDateS());
 return  ticketInterface.save(ticket1) ;
    }
    public int deleteTicket (Long id){
        Ticket ticket =ticketInterface.findById(id).orElse(null);
         ticketInterface.delete(ticket);
         return (0);
     }
    public Ticket updateTicketStatus(Long ticketId, Status newStatus) {
        Optional<Ticket> optionalTicket = ticketInterface.findById(ticketId);
        if (optionalTicket.isPresent()) {
            Ticket ticket = optionalTicket.get();
            ticket.setStatus(newStatus);

            // Set startDate when ticket moves to IN_PROGRESS
            if (newStatus == Status.IN_PROGRESS && ticket.getDateS() == null) {
                ticket.setDateS(LocalDate.now());
            }

            // Set endDate when ticket status is set to RESOLVED or CLOSED
            if ((newStatus == Status.RESOLVED || newStatus == Status.CLOSED) && ticket.getDateF() == null) {
                ticket.setDateF(LocalDate.now());
            }

            return ticketInterface.save(ticket);
        }
        return null; // Handle not found case based on your application's logic
    }




}
