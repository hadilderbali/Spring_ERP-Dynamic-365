package com.example.businesscentral.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ticket implements Serializable {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
    private String title;
  private   String description;
    private LocalDate createdDate = LocalDate.now(); // Automatically set creation date to current LocalDate
  private Integer duration;

  private LocalDate dateS ;
  private   LocalDate dateF ;
    @Enumerated(EnumType.STRING)
    private  Status status;
    @ManyToOne
    private  Project project;
  @ManyToOne
    private User assignedUser; // Adding a set to manage assigned users
  @JsonIgnore
  @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Event> events = new ArrayList<>();
  public void addEvent(Event event) {
    event.setTicket(this);
    events.add(event);
  }




}


