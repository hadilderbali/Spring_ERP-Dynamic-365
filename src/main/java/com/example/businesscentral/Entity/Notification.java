package com.example.businesscentral.Entity;

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
@Table(name = "notification") // Ensure table name matches

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
@ManyToMany(mappedBy = "notifications")
private Set<User> users;
}
