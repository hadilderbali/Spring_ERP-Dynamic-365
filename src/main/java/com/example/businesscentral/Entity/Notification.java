package com.example.businesscentral.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Notification implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idN;
    private String description;
    private LocalDate date;
    private boolean notification_read;
    private boolean enabled;
    @Enumerated(EnumType.STRING)
    private Category category;
    @JsonManagedReference
    @ManyToMany(mappedBy = "notifications", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<User> users;
    @JsonIgnore

    @ManyToOne
    private Event event;
}
