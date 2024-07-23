package com.example.businesscentral.Controller;

import com.example.businesscentral.Entity.Project;
import com.example.businesscentral.Entity.Status;
import com.example.businesscentral.Entity.Ticket;
import com.example.businesscentral.Service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/Ticket")

public class TicketController {
    private final TicketService ticketService;

    @PostMapping("/createTicket")
    public ResponseEntity<Ticket> createTicket(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam Long projectId) {
        Ticket createdTicket = ticketService.createTicket(title, description, projectId);
        return ResponseEntity.ok(createdTicket);
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
}
