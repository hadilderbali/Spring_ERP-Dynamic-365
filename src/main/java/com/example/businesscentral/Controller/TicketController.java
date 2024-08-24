package com.example.businesscentral.Controller;

import com.example.businesscentral.Entity.*;
import com.example.businesscentral.Service.TicketService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/Ticket")
@CrossOrigin("http://localhost:4200")

public class TicketController {
    private final TicketService ticketService;
@PostMapping("/createTicket")
    public ResponseEntity<Ticket> createTicket(
             @RequestBody TicketRequestDTO request) {
        try {
            Ticket ticket = ticketService.createTicket(
                    request.getTitle(),
                    request.getDescription(),
                    request.getProjectId(),
                    request.getDuration(),
                    request.getExistingEventId()
            );
            return ResponseEntity.ok(ticket);
        } catch (RuntimeException e) {
            // Handle exceptions such as "Project not found" or "Event not found"
            return ResponseEntity.badRequest().body(null);
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

  @PutMapping ("/status/{id}/{status}")
  public Ticket updateTicketStatus(@PathVariable("id") Long ticketId, @PathVariable("status") Status newStatus) {
  return ticketService.updateTicketStatus(ticketId,newStatus);}


    @GetMapping("/openTicketsByWeek")
    public ResponseEntity<Map<String, Long>> getOpenTicketsByWeek(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Map<String, Long> result = ticketService.getOpenTicketsByWeek(startDate, endDate);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/inProgressTicketsByWeek")
    public ResponseEntity<Map<String, Long>> getInProgressTicketsByWeek(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Map<String, Long> result = ticketService.getInProgressTicketsByWeek(startDate, endDate);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/resolvedTicketsByWeek")
    public ResponseEntity<Map<String, Long>> getResolvedTicketsByWeek(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Map<String, Long> result = ticketService.getResolvedTicketsByWeek(startDate, endDate);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/closedTicketsByWeek")
    public ResponseEntity<Map<String, Long>> getClosedTicketsByWeek(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Map<String, Long> result = ticketService.getClosedTicketsByWeek(startDate, endDate);
        return ResponseEntity.ok(result);
    }
    @GetMapping("/ReOpenedTicketsByWeek")
    public ResponseEntity<Map<String,Long>> getReOpenedTicketsByWeek(   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate){
        Map<String, Long> result = ticketService.getReOpenedTicketsByWeek(startDate,endDate);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/ticketsCountAndComparison")
    public ResponseEntity<Map<String, Object>> getTicketsCountAndComparison(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Map<String, Object> result = ticketService.getTicketsCountAndComparison(startDate, endDate);
        return ResponseEntity.ok(result);
    }
    @PostMapping("/{ticketId}/assign/{userId}")
    public ResponseEntity<Map<String, String>> assignTicketToUser(
            @PathVariable("ticketId") Long ticketId,
            @PathVariable("userId") Long userId) {
        if (ticketId == null || userId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("message", "Ticket ID and User ID must not be null."));
        }

        // Assume ticket assignment logic here

        return ResponseEntity.ok(Collections.singletonMap("message", "Ticket assigned successfully."));
    }
    @GetMapping("/capacityAndWorkload")
    public List<Map<String, Object>> getUserCapacityAndWorkload(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        return ticketService.getUserCapacityAndWorkload(startDate, endDate);
    }

    @GetMapping("/average-ticket-duration")
    public ResponseEntity<Map<String, Double>> getAverageTicketDuration(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
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

}