package com.example.businesscentral.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Project implements Serializable  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
private     Long id;
   private String nameP;
   private String description;
   private LocalDate createdDate = LocalDate.now(); // Automatically set creation date to current LocalDate

    @JsonIgnore
    @OneToMany(mappedBy="project")
    private Set<Ticket> tickets;
    @JsonBackReference
    @ManyToMany
    private Set<Team> teams = new HashSet<>();
}
