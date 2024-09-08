package com.example.businesscentral.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ChangeLogDTO {
    private Long id;
    private Long ticketId;
    private String ticketTitle;
    private Long userId;
    private String userName;
    private String oldStatus;
    private String newStatus;
    private LocalDateTime changeDate;
    private String description;

    }


