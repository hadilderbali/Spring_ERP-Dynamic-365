package com.example.businesscentral.Service;

import com.example.businesscentral.Entity.*;
import com.example.businesscentral.EventRepository;
import com.example.businesscentral.Repository.ProjectRepository;
import com.example.businesscentral.Repository.TicketRepository;
import com.example.businesscentral.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class TicketService {
    private static final double CAPACITY = 1000; // Example capacity

    final TicketRepository ticketInterface;
    final ProjectRepository projectInterface;
    final  EmailService emailService;
     final UserRepository userRepository;
   final private EventRepository eventRepository;

    public Ticket createTicket(String title, String description, Long projectId, Integer duration, Long existingEventId) {
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
            ticket.setDuration(duration);

            if (existingEventId != null) {
                // Associate an existing event with the ticket
                Optional<Event> optionalEvent = eventRepository.findById(existingEventId);
                if (optionalEvent.isPresent()) {
                    Event event = optionalEvent.get();
                    event.setTicket(ticket); // Associate the event with the ticket
                    ticket.addEvent(event); // Add the event to the ticket
                } else {
                    throw new RuntimeException("Event not found");
                }
            } else {
                // Create and add a new event for ticket creation
                Event event = new Event();
                event.setName("Ticket Created"); // Descriptive name
                event.setDateEvent(LocalDate.now());
                event.setTicket(ticket); // Associate the event with the ticket
                ticket.addEvent(event);
            }

            // Save the ticket (and associated events due to cascading)
            return ticketInterface.save(ticket);
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
            Status oldStatus = ticket.getStatus();
            ticket.setStatus(newStatus);

            // Set startDate when ticket moves to IN_PROGRESS or ASSIGNED
            if ((newStatus == Status.IN_PROGRESS || newStatus == Status.ASSIGNED) && ticket.getDateS() == null) {
                ticket.setDateS(LocalDate.now());
            }

            // Set endDate when ticket status is set to RESOLVED or CLOSED
            if ((newStatus == Status.RESOLVED || newStatus == Status.CLOSED) && ticket.getDateF() == null) {
                ticket.setDateF(LocalDate.now());
            }

            // Create and add the event for status change
            Event event = new Event();
            event.setName("Status changed from " + oldStatus + " to " + newStatus);
            event.setDateEvent(LocalDate.now());
            event.setTicket(ticket); // Set the associated ticket
            ticket.addEvent(event);

            // Save the ticket (and associated events due to cascading)
            return ticketInterface.save(ticket);
        }
        return null; // Handle not found case based on your application's logic
    }


    // Service method for Open tickets
    public Map<String, Long> getOpenTicketsByWeek(LocalDate startDate, LocalDate endDate) {
        List<Ticket> tickets = ticketInterface.findByDateSBetween(startDate, endDate);

        return tickets.stream()
                .filter(ticket -> ticket.getStatus() == Status.NEW)
                .collect(Collectors.groupingBy(ticket -> getWeekOfYear(ticket.getDateS()), Collectors.counting()));
    }

    // Service method for In Progress tickets
    public Map<String, Long> getInProgressTicketsByWeek(LocalDate startDate, LocalDate endDate) {
        List<Ticket> tickets = ticketInterface.findByDateSBetween(startDate, endDate);

        return tickets.stream()
                .filter(ticket -> ticket.getStatus() == Status.IN_PROGRESS)
                .collect(Collectors.groupingBy(ticket -> getWeekOfYear(ticket.getDateS()), Collectors.counting()));
    }

    // Service method for Resolved tickets
    public Map<String, Long> getResolvedTicketsByWeek(LocalDate startDate, LocalDate endDate) {
        List<Ticket> tickets = ticketInterface.findByDateSBetween(startDate, endDate);

        return tickets.stream()
                .filter(ticket -> ticket.getStatus() == Status.RESOLVED)
                .collect(Collectors.groupingBy(ticket -> getWeekOfYear(ticket.getDateS()), Collectors.counting()));
    }

    // Service method for Closed tickets
    public Map<String, Long> getClosedTicketsByWeek(LocalDate startDate, LocalDate endDate) {
        List<Ticket> tickets = ticketInterface.findByDateSBetween(startDate, endDate);

        return tickets.stream()
                .filter(ticket -> ticket.getStatus() == Status.CLOSED)
                .collect(Collectors.groupingBy(ticket -> getWeekOfYear(ticket.getDateS()), Collectors.counting()));
    }
    // Service method for ReOpened tickets
    public Map<String, Long> getReOpenedTicketsByWeek(LocalDate startDate, LocalDate endDate) {
        List<Ticket> tickets = ticketInterface.findByDateSBetween(startDate, endDate);

        return tickets.stream()
                .filter(ticket -> ticket.getStatus() == Status.REOPENED)
                .collect(Collectors.groupingBy(ticket -> getWeekOfYear(ticket.getDateS()), Collectors.counting()));
    }



    private String getWeekOfYear(LocalDate date) {
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        return String.format("%d-W%02d", date.getYear(), date.get(weekFields.weekOfWeekBasedYear()));
    }

    public long getTotalTicketsByDuration(LocalDate startDate, LocalDate endDate) {
        List<Ticket> tickets = ticketInterface.findByDateSBetween(startDate, endDate);
        return tickets.size();
    }


    // Method to get the ticket count and compare it with capacity
    public Map<String, Object> getTicketsCountAndComparison(LocalDate startDate, LocalDate endDate) {
        long totalTickets = getTotalTicketsByDuration(startDate, endDate);

        double percentageOfCapacity = (totalTickets / CAPACITY) * 100;

        Map<String, Object> result = new HashMap<>();
        result.put("totalTickets", totalTickets);
        result.put("capacity", CAPACITY);
        result.put("percentageOfCapacity", percentageOfCapacity);

        return result;
    }

    public void assignTicketToUser(Long ticketId, Long userId) {
        // Fetch the ticket by ID
        Ticket ticket = ticketInterface.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));

        // Fetch the user by ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Get the project associated with the ticket
        Project project = ticket.getProject();
        if (project == null) {
            throw new IllegalStateException("Ticket is not associated with any project");
        }

        // Check if the user is part of any team associated with the project
        Set<Team> teams = project.getTeams();
        if (teams == null || teams.isEmpty()) {
            throw new IllegalStateException("Project does not have any associated teams");
        }

        boolean userInTeam = teams.stream()
                .anyMatch(team -> team.getUsers().contains(user));

        if (!userInTeam) {
            throw new IllegalStateException("User is not part of any team associated with the project");
        }

        // Assign the ticket to the user
        ticket.setAssignedUser(user);

        // Save the updated ticket
        ticketInterface.save(ticket);
    }

    public List<Map<String, Object>> getUserCapacityAndWorkload(LocalDate startDate, LocalDate endDate) {
        List<User> users = userRepository.findAll();
        long totalDays = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1; // +1 to include end date

        return users.stream().map(user -> {
            int totalCapacity = user.getCapacity();
            // Count tickets assigned to the user within the date range and calculate total duration
            List<Ticket> tickets = ticketInterface.findTicketsByAssignedUserAndDateRange(user, startDate, endDate);
            int totalTicketDuration = tickets.stream().mapToInt(Ticket::getDuration).sum(); // Sum of all ticket durations
            double workload = (double) totalTicketDuration / totalDays; // Workload per day
            int remainingCapacity = totalCapacity - (int) workload;
            double percentageOfCapacity = (totalTicketDuration / (double) totalCapacity) * 100; // Percentage of capacity used

            Map<String, Object> userCapacityData = new HashMap<>();
            userCapacityData.put("username", user.getUsername());
            userCapacityData.put("totalCapacity", totalCapacity);
            userCapacityData.put("workload", workload);
            userCapacityData.put("remainingCapacity", remainingCapacity);
            userCapacityData.put("percentageOfCapacity", percentageOfCapacity); // Add this line

            return userCapacityData;
        }).collect(Collectors.toList());
    }
    public double getAverageTicketDuration(LocalDate startDate, LocalDate endDate) {
        List<Ticket> tickets = ticketInterface.findTicketsByDateRange(startDate, endDate);
        long totalDuration = 0;
        int count = 0;

        for (Ticket ticket : tickets) {
            if (ticket.getDateS() != null && ticket.getDateF() != null && ticket.getStatus() == Status.RESOLVED) {
                Duration duration = Duration.between(ticket.getDateS().atStartOfDay(), ticket.getDateF().atStartOfDay());
                totalDuration += duration.toDays();
                count++;
            }
        }

        return count > 0 ? (double) totalDuration / count : 0;
    }


    public List<User> getUsersByTicketId(Long ticketId) {
        Ticket ticket = ticketInterface.findById(ticketId).orElseThrow();
        Set<Team> teams = ticket.getProject().getTeams();
        Set<User> users = new HashSet<>();
        for (Team team : teams) {
            users.addAll(team.getUsers());
        }
        return new ArrayList<>(users);
    }
}






