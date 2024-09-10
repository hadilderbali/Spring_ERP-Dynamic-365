package com.example.businesscentral.Service;

import com.example.businesscentral.Entity.*;
import com.example.businesscentral.Repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    @Value("${upload.dir}")
    private String uploadDir;
    @Value("${file.upload-dir}")
    private String uploadDirPath;
    final TicketRepository ticketInterface;
    final ProjectRepository projectInterface;
    final EmailService emailService;
    final UserRepository userRepository;
    final private EventRepository eventRepository;
final private ChangeLogRepository changeLogRepository;
    public Ticket createTicket(String title, String description, Long projectId, Long existingEventId, MultipartFile file) throws IOException {
        Optional<Project> optionalProject = projectInterface.findById(projectId);
        if (optionalProject.isPresent()) {
            Project project = optionalProject.get();

            // Create the ticket
            Ticket ticket = new Ticket();
            ticket.setTitle(title);
            ticket.setDescription(description);
            ticket.setProject(project);
            ticket.setCreatedDate(LocalDate.now());
            ticket.setStatus(Status.NEW); // Ensure this matches your enum

            if (file != null && !file.isEmpty()) {
                String attachmentPath = saveFile(file);
                ticket.setAttachmentPath(attachmentPath); // Set the file path in the ticket
            }

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

    private static final String[] ALLOWED_EXTENSIONS = {".pdf"};

    private boolean isValidFileType(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename != null) {
            for (String extension : ALLOWED_EXTENSIONS) {
                if (filename.toLowerCase().endsWith(extension)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String saveFile(MultipartFile file) throws IOException {
        if (!isValidFileType(file)) {
            throw new IllegalArgumentException("Invalid file type. Only PDF files are allowed.");
        }

        File uploadDirFile = new File(uploadDirPath);
        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs();
        }

        String filePath = uploadDirPath + File.separator + file.getOriginalFilename();
        File destinationFile = new File(filePath);
        file.transferTo(destinationFile);
        return filePath;
    }


    public Ticket getTicket(Long id) {
        return ticketInterface.findById(id).orElse(null);
    }

    public List<Ticket> getAllTicket() {
        return ticketInterface.findAll();
    }

    public Ticket updateTicket(Long id, Ticket ticket) {
        Ticket ticket1 = ticketInterface.findById(id).orElse(null);
        ticket1.setTitle(ticket.getTitle());
        ticket1.setDescription(ticket.getDescription());
        ticket1.setDateF(ticket.getDateF());
        ticket1.setCreatedDate(ticket.getCreatedDate());
        ticket1.setDateS(ticket.getDateS());
        return ticketInterface.save(ticket1);
    }

    public int deleteTicket(Long id) {
        Ticket ticket = ticketInterface.findById(id).orElse(null);
        ticketInterface.delete(ticket);
        return (0);
    }

   /* public Ticket updateTicketStatus(Long ticketId, Status newStatus) {
        Optional<Ticket> optionalTicket = ticketInterface.findById(ticketId);
        if (optionalTicket.isPresent()) {
            Ticket ticket = optionalTicket.get();
            Status oldStatus = ticket.getStatus();
            ticket.setStatus(newStatus);

            // Set startDate when ticket moves to IN_PROGRESS or ASSIGNED
            if ((newStatus == Status.IN_PROGRESS || newStatus == Status.ASSIGNED) && ticket.getDateS() == null) {
                ticket.setDateS(LocalDateTime.now());
            }

            // Set endDate when ticket status is set to RESOLVED or CLOSED
            if ((newStatus == Status.RESOLVED || newStatus == Status.CLOSED) && ticket.getDateF() == null) {
                ticket.setDateF(LocalDate.now().atStartOfDay());
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
    }*/


    // Service method for Open tickets
    public Map<String, Long> getOpenTicketsByWeek(LocalDateTime startDate, LocalDateTime endDate) {
        List<Ticket> tickets = ticketInterface.findByDateSBetween(startDate, endDate);

        return tickets.stream()
                .filter(ticket -> ticket.getStatus() == Status.NEW)
                .collect(Collectors.groupingBy(ticket -> getWeekOfYear(LocalDate.from(ticket.getDateS())), Collectors.counting()));
    }

    // Service method for In Progress tickets
    public Map<String, Long> getInProgressTicketsByWeek(LocalDateTime startDate, LocalDateTime endDate) {
        List<Ticket> tickets = ticketInterface.findByDateSBetween(startDate, endDate);

        return tickets.stream()
                .filter(ticket -> ticket.getStatus() == Status.IN_PROGRESS)
                .collect(Collectors.groupingBy(ticket -> getWeekOfYear(LocalDate.from(ticket.getDateS())), Collectors.counting()));
    }

    // Service method for Resolved tickets
    public Map<String, Long> getResolvedTicketsByWeek(LocalDateTime startDate, LocalDateTime endDate) {
        List<Ticket> tickets = ticketInterface.findByDateSBetween(startDate, endDate);

        return tickets.stream()
                .filter(ticket -> ticket.getStatus() == Status.RESOLVED)
                .collect(Collectors.groupingBy(ticket -> getWeekOfYear(LocalDate.from(ticket.getDateS())), Collectors.counting()));
    }

    // Service method for Closed tickets
    public Map<String, Long> getClosedTicketsByWeek(LocalDateTime startDate, LocalDateTime endDate) {
        List<Ticket> tickets = ticketInterface.findByDateSBetween(startDate, endDate);

        return tickets.stream()
                .filter(ticket -> ticket.getStatus() == Status.CLOSED)
                .collect(Collectors.groupingBy(ticket -> getWeekOfYear(LocalDate.from(ticket.getDateS())), Collectors.counting()));
    }

    // Service method for ReOpened tickets
    public Map<String, Long> getReOpenedTicketsByWeek(LocalDateTime startDate, LocalDateTime endDate) {
        List<Ticket> tickets = ticketInterface.findByDateSBetween(startDate, endDate);

        return tickets.stream()
                .filter(ticket -> ticket.getStatus() == Status.REOPENED)
                .collect(Collectors.groupingBy(ticket -> getWeekOfYear(LocalDate.from(ticket.getDateS())), Collectors.counting()));
    }


    private String getWeekOfYear(LocalDate date) {
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        return String.format("%d-W%02d", date.getYear(), date.get(weekFields.weekOfWeekBasedYear()));
    }

    public long getTotalTicketsByDuration(LocalDateTime startDate, LocalDateTime endDate) {
        List<Ticket> tickets = ticketInterface.findByDateSBetween(startDate, endDate);
        return tickets.size();
    }


    // Method to get the ticket count and compare it with capacity
    public Map<String, Object> getTicketsCountAndComparison(LocalDateTime startDate, LocalDateTime endDate) {
        long totalTickets = getTotalTicketsByDuration(startDate, endDate);

        double percentageOfCapacity = (totalTickets / CAPACITY) * 100;

        Map<String, Object> result = new HashMap<>();
        result.put("totalTickets", totalTickets);
        result.put("capacity", CAPACITY);
        result.put("percentageOfCapacity", percentageOfCapacity);

        return result;
    }


    public List<Map<String, Object>> getUserCapacityAndWorkload(LocalDateTime startDate, LocalDateTime endDate) {
        // Swap dates if startDate is after endDate
        if (startDate.isAfter(endDate)) {
            LocalDateTime temp = startDate;
            startDate = endDate;
            endDate = temp;
        }

        List<User> users = userRepository.findAll();
        long totalDays = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1; // +1 to include end date

        LocalDateTime finalStartDate = startDate;
        LocalDateTime finalEndDate = endDate;

        return users.stream().map(user -> {
            int totalCapacity = user.getCapacity();
            List<Ticket> tickets = ticketInterface.findTicketsByAssignedUserAndDateRange(user, finalStartDate, finalEndDate);

            // Calculate total ticket duration in hours
            long totalTicketDuration = tickets.stream().mapToLong(ticket -> {
                LocalDateTime ticketStart = ticket.getDateS();
                LocalDateTime ticketEnd = ticket.getDateF();

                if (ticketStart != null && ticketEnd != null) {
                    // Ensure ticketStart is before ticketEnd
                    if (ticketStart.isAfter(ticketEnd)) {
                        LocalDateTime temp = ticketStart;
                        ticketStart = ticketEnd;
                        ticketEnd = temp;
                    }

                    Duration duration = Duration.between(ticketStart, ticketEnd);
                    return Math.max(duration.toHours(), 0);  // Convert to hours and ensure non-negative
                }
                return 0; // Handle null dates or invalid durations
            }).sum();

            // Debugging: Print total ticket duration
            System.out.println("Total ticket duration (hours): " + totalTicketDuration);

            // Calculate workload and percentage of capacity
            double workload = totalDays > 0 ? (double) totalTicketDuration / totalDays : 0;
            double percentageOfCapacity = totalCapacity > 0 ? (double) totalTicketDuration / totalCapacity * 100 : 0;

            // Prepare user capacity data
            Map<String, Object> userCapacityData = new HashMap<>();
            userCapacityData.put("username", user.getUsername());
            userCapacityData.put("totalCapacity", totalCapacity);
            userCapacityData.put("totalHoursWorked", totalTicketDuration); // Absolute value is not needed if correctly calculated
            userCapacityData.put("workload", workload);
            userCapacityData.put("remainingCapacity", totalCapacity - (int) workload);
            userCapacityData.put("percentageOfCapacity", percentageOfCapacity);

            // Debugging: Print user capacity data
            System.out.println("User capacity data: " + userCapacityData);

            return userCapacityData;
        }).collect(Collectors.toList());
    }

    public double getAverageTicketDuration(LocalDateTime startDate, LocalDateTime endDate) {
        List<Ticket> tickets = ticketInterface.findTicketsByDateRange(startDate, endDate);
        long totalDuration = 0;
        int count = 0;

        for (Ticket ticket : tickets) {
            if (ticket.getDateS() != null && ticket.getDateF() != null) {
                Duration duration = Duration.between(ticket.getDateS(), ticket.getDateF());
                totalDuration += Math.max(duration.toHours(), 0); // Ensure non-negative duration
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

    public Resource getFile(String fileName) throws IOException {
        // Only extract the file name, not the full path
        String fileNameOnly = Paths.get(fileName).getFileName().toString();
        Path filePath = Paths.get(uploadDir).resolve(fileNameOnly).normalize();
        Resource resource = new UrlResource(filePath.toUri());
        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new IOException("File not found or not readable: " + fileName);
        }
    }

    public List<Ticket> getTicketsByEventAndRoles(String eventName) {
        return ticketInterface.findByEventNameAndRoleNames(eventName);
    }

    @Transactional
    public void assignUsersToTicket(Long ticketId, Set<String> usernames, LocalDateTime deadline) {
        Ticket ticket = ticketInterface.findById(ticketId).orElse(null);
        if (ticket == null) {
            throw new RuntimeException("Ticket not found");
        }

        Set<User> users = new HashSet<>();
        for (String username : usernames) {
            // Use the repository method to find users by username
            List<User> foundUsers = userRepository.findByUsernameContainingIgnoreCase(username);
            if (!foundUsers.isEmpty()) {
                for (User user : foundUsers) {
                    users.add(user);
                    // Add the ticket to the user's set of tickets
                    user.getTickets().add(ticket);

                    System.out.println("Found user: " + user.getUsername());
                }
            } else {
                System.out.println("User with username " + username + " not found");
            }
        }

        // Set the ticket's assigned users
        ticket.setAssignedUser(users);

        // Set start date to current date if not already set
        if (ticket.getDateS() == null) {
            ticket.setDateS(LocalDateTime.now());
        }

        // Set the deadline
        ticket.setDeadline(deadline);
        ticket.setStatus(Status.ASSIGNED);
        // Log before saving
        System.out.println("Ticket before save: " + ticket);

        // Save the updated ticket
        ticketInterface.save(ticket);

        // Log after saving
        System.out.println("Ticket after save: " + ticket);
    }



    public Map<String, Map<String, Integer>> calculateUserWorkload() {
        List<Ticket> tickets = ticketInterface.findAll();
        List<User> users = userRepository.findAll(); // Fetch all users
        Map<String, Map<String, Integer>> userWorkloadMap = new HashMap<>();

        // Initialize user capacities
        Map<String, Integer> userCapacityMap = new HashMap<>();
        for (User user : users) {
            userCapacityMap.put(user.getUsername(), user.getCapacity() != 0 ? user.getCapacity() : 40); // Default capacity if null
        }

        for (Ticket ticket : tickets) {
            long hoursWorked = calculateDurationInHours(ticket.getDateS(), ticket.getDateF());
            for (User user : ticket.getAssignedUser()) {
                String username = user.getUsername();
                Map<String, Integer> workloadData = userWorkloadMap.getOrDefault(username, new HashMap<>());
                int totalHoursWorked = workloadData.getOrDefault("totalHoursWorked", 0);
                workloadData.put("totalHoursWorked", totalHoursWorked + (int) hoursWorked);

                // Add or update capacity
                workloadData.put("capacity", userCapacityMap.getOrDefault(username, 40)); // Use default capacity if not found

                userWorkloadMap.put(username, workloadData);
            }
        }

        return userWorkloadMap;
    }

    private long calculateDurationInHours(LocalDateTime startDate, LocalDateTime finalDate) {
        if (startDate == null || finalDate == null) {
            return 0;
        }
        Duration duration = Duration.between(finalDate,  startDate);
        return duration.toHours();
    }

    public List<Ticket> getTicketsByUserId(Long userId) {
        return ticketInterface.findByAssignedUserId(userId);
    }

    @Transactional
    public Ticket updateTicketStatus(Long ticketId, Status status, Long userId) {
        Optional<Ticket> optionalTicket = ticketInterface.findById(ticketId);
        if (optionalTicket.isEmpty()) {
            throw new IllegalArgumentException("Ticket not found with ID: " + ticketId);
        }

        Ticket ticket = optionalTicket.get();

        // Get the old status before the change
        Status oldStatus = ticket.getStatus();

        // Update the status of the ticket
        ticket.setStatus(status);

        // Handle finalDate based on the new status
        if (status == Status.RESOLVED || status == Status.CLOSED) {
            ticket.setDateF(LocalDateTime.now());  // Set finalDate for RESOLVED/CLOSED
        } else if (status == Status.REOPENED) {
            ticket.setDateF(null);  // Reset finalDate for REOPENED
        }

        // Save the updated ticket
        Ticket updatedTicket = ticketInterface.save(ticket);

        // Fetch the user who made the change
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        // Log the status change in the ChangeLog
        ChangeLog changeLog = new ChangeLog(ticket, user, oldStatus.toString(), status.toString(), "Status updated from " + oldStatus + " to " + status);
        changeLogRepository.save(changeLog);


        return updatedTicket;
    }

    public List<Ticket> getDoneTickets(Long userId) {
        return ticketInterface.findByAssignedUserAndStatusIn(userId, Arrays.asList(Status.RESOLVED, Status.CLOSED));
    }

    // For Doing Tasks (In Progress)
    public List<Ticket> getInProgressTickets(Long userId) {
        return ticketInterface.findByAssignedUserAndStatus(userId, Status.IN_PROGRESS);
    }

    // For To Do Tasks (New or Assigned)
    public List<Ticket> getToDoTickets(Long userId) {
        return ticketInterface.findByAssignedUserAndStatusIn(userId, Arrays.asList(Status.NEW, Status.ASSIGNED));
    }
    public List<ChangeLogDTO> findByTicketId(Long ticketId) {
        List<ChangeLog> changeLogs = changeLogRepository.findByTicketId(ticketId);

        return changeLogs.stream().map(changeLog -> {
            ChangeLogDTO dto = new ChangeLogDTO();
            dto.setId(changeLog.getId());
            dto.setOldStatus(changeLog.getOldStatus());
            dto.setNewStatus(changeLog.getNewStatus());
            dto.setChangeDate(changeLog.getChangeDate());
            dto.setDescription(changeLog.getDescription());

            // Set Ticket details
            if (changeLog.getTicket() != null) {
                dto.setTicketId(changeLog.getTicket().getId());
                dto.setTicketTitle(changeLog.getTicket().getTitle());
            }

            // Set User details
            if (changeLog.getUser() != null) {
                dto.setUserId(changeLog.getUser().getUserid());
                dto.setUserName(changeLog.getUser().getUsername());
            }

            return dto;
        }).collect(Collectors.toList());
    }
    public void uploadFileForTicket(Long ticketId, MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // Ensure the upload directory exists
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Save the file
        String filename = file.getOriginalFilename();
        File targetFile = new File(uploadDir + File.separator + filename);
        file.transferTo(targetFile);

        // Update the ticket with the file path
        Optional<Ticket> optionalTicket = ticketInterface.findById(ticketId);
        if (optionalTicket.isPresent()) {
            Ticket ticket = optionalTicket.get();
            ticket.setFrontOfficeAttachmentPath(filename); // Save only the filename or path relative to uploadDir
            ticketInterface.save(ticket);
        } else {
            throw new IllegalArgumentException("Ticket not found");
        }
    }
    public Resource loadFileAsResource(String fileName) throws IOException {
        Path filePath = Paths.get(uploadDirPath).resolve(fileName).normalize();
        Resource resource = new UrlResource(filePath.toUri());
        if (resource.exists()) {
            return resource;
        } else {
            throw new IOException("File not found " + fileName);
        }
    }
    public Resource loadFileForTicket(Long ticketId) throws IOException {
        Optional<Ticket> optionalTicket = ticketInterface.findById(ticketId);
        if (optionalTicket.isPresent()) {
            Ticket ticket = optionalTicket.get();
            String filename = ticket.getFrontOfficeAttachmentPath();
            if (filename == null || filename.isEmpty()) {
                throw new IllegalArgumentException("No file associated with this ticket.");
            }

            Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new IOException("File not found " + filename);
            }
        } else {
            throw new IllegalArgumentException("Ticket not found");
        }
    }
}