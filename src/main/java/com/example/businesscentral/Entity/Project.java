package com.example.businesscentral.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
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
    @JsonIgnore
    @OneToMany(mappedBy="project")
    private Set<Ticket> tickets;
@JsonIgnore
     @ManyToMany
    private Set<Team>teams;
}
