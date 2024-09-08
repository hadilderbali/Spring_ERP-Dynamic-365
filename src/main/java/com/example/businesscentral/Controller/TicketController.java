package com.example.businesscentral.Controller;

import com.example.businesscentral.Entity.*;
import com.example.businesscentral.Service.ProjectService;
import com.example.businesscentral.Service.TicketService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/Ticket")
@CrossOrigin(origins = "http://localhost:4200 ", allowedHeaders = "*")

public class TicketController {
    private final TicketService ticketService;
    private final ProjectService projectService;
    @PostMapping("/createTicket")
public ResponseEntity<?> createTicket(
        @RequestParam("title") String title,
        @RequestParam("description") String description,
        @RequestParam("projectId") Long projectId,
        @RequestParam("duration") Integer duration,
        @RequestParam(value = "existingEventId", required = false) Long existingEventId,
        @RequestParam(value = "file", required = false) MultipartFile file) {

    try {
        Ticket ticket = ticketService.createTicket(title, description, projectId, duration, existingEventId, file);
        return ResponseEntity.ok(Map.of("message", "Ticket created successfully with ID: " + ticket.getId()));
    } catch (IOException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error creating ticket: " + e.getMessage()));
    }
}
    @GetMapping("/files")
    public ResponseEntity<Resource> getFile(@RequestParam String filePath) {
        try {
            Path path = Paths.get(filePath).normalize();

            // Ensure that the path is safe
            if (!Files.exists(path) || !Files.isReadable(path)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            Resource resource = new UrlResource(path.toUri());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/updateticket/{id}")
    public Ticket updateTicket(@PathVariable Long id, @RequestBody Ticket ticket) {
        return ticketService.updateTicket(id , ticket);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
    }

    @GetMapping("/getAll")
    public List<Ticket> getAllTickets() {
        return ticketService.getAllTicket();
    }
    @GetMapping("/getById/{id}")
    public  Ticket GetTicketById(@PathVariable Long id){
        return ticketService.getTicket(id);
    }

    @GetMapping("/openTicketsByWeek")
    public ResponseEntity<Map<String, Long>> getOpenTicketsByWeek(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        Map<String, Long> result = ticketService.getOpenTicketsByWeek(startDate, endDate);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/inProgressTicketsByWeek")
    public ResponseEntity<Map<String, Long>> getInProgressTicketsByWeek(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate){
        Map<String, Long> result = ticketService.getInProgressTicketsByWeek(startDate, endDate);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/resolvedTicketsByWeek")
    public ResponseEntity<Map<String, Long>> getResolvedTicketsByWeek(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        Map<String, Long> result = ticketService.getResolvedTicketsByWeek(startDate, endDate);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/closedTicketsByWeek")
    public ResponseEntity<Map<String, Long>> getClosedTicketsByWeek(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        Map<String, Long> result = ticketService.getClosedTicketsByWeek(startDate, endDate);
        return ResponseEntity.ok(result);
    }
    @GetMapping("/ReOpenedTicketsByWeek")
    public ResponseEntity<Map<String,Long>> getReOpenedTicketsByWeek(   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate){
        Map<String, Long> result = ticketService.getReOpenedTicketsByWeek(startDate,endDate);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/ticketsCountAndComparison")
    public ResponseEntity<Map<String, Object>> getTicketsCountAndComparison(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime endDate) {
        Map<String, Object> result = ticketService.getTicketsCountAndComparison(startDate, endDate);
        return ResponseEntity.ok(result);
    }
    @PostMapping("/{ticketId}/assign")
    public void assignUsersToTicket(
            @PathVariable Long ticketId,
            @RequestParam Set<String> usernames,
            @RequestParam LocalDateTime deadline) {
        ticketService.assignUsersToTicket(ticketId, usernames, deadline);
    }
    @GetMapping("/capacityAndWorkload")
    public List<Map<String, Object>> getUserCapacityAndWorkload(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        return ticketService.getUserCapacityAndWorkload(startDate, endDate);
    }

    @GetMapping("/average-ticket-duration")
    public ResponseEntity<Map<String, Double>> getAverageTicketDuration(@RequestParam LocalDateTime startDate, @RequestParam LocalDateTime endDate) {
        double averageDuration = ticketService.getAverageTicketDuration(startDate, endDate);
        Map<String, Double> response = new HashMap<>();
        response.put("averageDuration", averageDuration);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/tickets/{ticketId}/users")
    public ResponseEntity<List<User>> getUsersByTicket(@PathVariable Long ticketId) {
        List<User> users = ticketService.getUsersByTicketId(ticketId);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/getTicketTitle")
    public List<Ticket> getTicketsByEventNameAndRoleNames(
            @RequestParam("eventName") String eventName
    ) {
        return ticketService.getTicketsByEventAndRoles(eventName);
    }
    @GetMapping("/userWorkload")
    public Map<String, Map<String, Integer>> getUserWorkload() {
        return ticketService.calculateUserWorkload();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Ticket>> getTicketsByUserId(@PathVariable Long userId) {
        List<Ticket> tickets = ticketService.getTicketsByUserId(userId);
        return ResponseEntity.ok(tickets);
    }

    @PatchMapping("/{ticketId}/status/{status}")
    public ResponseEntity<Ticket> updateTicketStatus(
            @PathVariable Long ticketId,
            @PathVariable String status,
            @RequestParam Long userId) { // Assuming userId is passed as a query param

        try {
            // Convert the status string to uppercase and match with Status enum
            Status ticketStatus = Status.valueOf(status.toUpperCase());

            // Update the ticket status with the provided userId
            Ticket updatedTicket = ticketService.updateTicketStatus(ticketId, ticketStatus, userId);

            return ResponseEntity.ok(updatedTicket);
        } catch (IllegalArgumentException e) {
            // Handle invalid status
            return ResponseEntity.badRequest().body(null);
        }
    }
    @GetMapping("/done/{userId}")
    public ResponseEntity<List<Ticket>> getDoneTickets(@PathVariable Long userId) {
        List<Ticket> tickets = ticketService.getDoneTickets(userId);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/in-progress/{userId}")
    public ResponseEntity<List<Ticket>> getInProgressTickets(@PathVariable Long userId) {
        List<Ticket> tickets = ticketService.getInProgressTickets(userId);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/to-do/{userId}")
    public ResponseEntity<List<Ticket>> getToDoTickets(@PathVariable Long userId) {
        List<Ticket> tickets = ticketService.getToDoTickets(userId);
        return ResponseEntity.ok(tickets);
    }
    @GetMapping("/Logs/{ticketId}")
    public List<ChangeLogDTO> getChangeLogsByTicketId(@PathVariable Long ticketId) {
        return ticketService.findByTicketId(ticketId);
    }
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("ticketId") Long ticketId) {

        Map<String, String> response = new HashMap<>();

        if (file.isEmpty() || ticketId == null) {
            response.put("message", "File or ticket ID is missing");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            ticketService.uploadFileForTicket(ticketId, file);
            response.put("message", "File uploaded successfully");
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("message", "Error uploading file");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (IllegalArgumentException e) {
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    @GetMapping("/download/{ticketId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long ticketId) {
        try {
            Resource file = ticketService.loadFileForTicket(ticketId);
            String filename = file.getFilename();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(file);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

}