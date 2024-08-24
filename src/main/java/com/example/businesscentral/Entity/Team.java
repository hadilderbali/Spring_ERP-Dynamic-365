package com.example.businesscentral.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Team implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;
   private String nameT;
 private Integer membreInteger;
    @JsonManagedReference
    @ManyToMany(mappedBy = "teams")
    private Set<Project>projects;
    @JsonManagedReference // Manage serialization of the users list

    @ManyToMany(mappedBy = "teams" )
    private Set<User> users = new HashSet<>();
}
