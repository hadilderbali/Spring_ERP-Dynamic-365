package com.example.businesscentral.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Event implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // Event name like "Ticket Created", "Issue Escalated", etc.
    private LocalDate dateEvent = LocalDate.now();  // When the event occurred
    @JsonIgnore

    @ManyToOne(fetch = FetchType.LAZY)

    @JoinColumn(name = "ticket_id")
    private Ticket ticket;
    @JsonIgnore
    @OneToMany(mappedBy = "event")
    private Set<Notification> notifications;
    // Additional fields like description, eventType, etc. can be added as needed
}
