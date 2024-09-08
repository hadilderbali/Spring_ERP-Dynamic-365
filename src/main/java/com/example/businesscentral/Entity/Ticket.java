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
  private LocalDateTime dateS ;
  private   LocalDateTime dateF ;
  private   LocalDateTime deadline ;
  private String frontOfficeAttachmentPath;
  private String attachmentPath;  // New field to store file path or URL

  @Enumerated(EnumType.STRING)
    private  Status status;
    @ManyToOne
    private  Project project;
  @JsonManagedReference

  @ManyToMany(mappedBy = "tickets")
  private Set<User> assignedUser;
  @JsonIgnore

  @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Event> events = new ArrayList<>();
  @JsonIgnore
  @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Comment> comments;
  @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference
  private Set<ChangeLog> changeLogs;

  public void addEvent(Event event) {
    event.setTicket(this);
    events.add(event);
  }




}


