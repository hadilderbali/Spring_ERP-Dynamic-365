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
    private Long id;
    private String nameT;
    private Integer membreInteger;
    @JsonIgnore
    @ManyToMany(mappedBy = "teams")
    private Set<Project> projects = new HashSet<>();

    @ManyToMany(mappedBy = "teams")
    private Set<User> users = new HashSet<>();
}
