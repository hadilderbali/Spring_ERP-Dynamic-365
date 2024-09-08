package com.example.businesscentral.Entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
public class TicketRequestDTO {

    private Long id;
    private String title;
    private String description;
    private LocalDateTime createdDate;
    private Integer duration;
    private LocalDateTime dateS;
    private LocalDateTime dateF;
    private LocalDateTime deadline;
    private String attachmentPath;
    private String status;
    private Project project;
    private List<Long> assignedUserIds;
    private List<Long> commentIds;

    public TicketRequestDTO(Long id, String title, String description, String string, Status status) {
    }
}
