package com.example.businesscentral.Entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter

public class NotificationRequest {
    private String description;
    private boolean enabled;
    private Category category; // Added category field
    private List<Long> userIds; // New field for user IDs

}
