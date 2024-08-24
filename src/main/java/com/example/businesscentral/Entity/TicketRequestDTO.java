package com.example.businesscentral.Entity;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class TicketRequestDTO {

    private String title;

    private String description;

    private Long projectId;

    private Integer duration;

    private Long existingEventId; // Optional field for associating an existing event
}
