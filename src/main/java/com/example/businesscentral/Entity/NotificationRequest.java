package com.example.businesscentral.Entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter

public class NotificationRequest {
    private String eventName;  // Use event name to fetch the event
    private Set<String> roleNames;  // Role names to fetch roles
    private String description;
    private Category category;
    private boolean isEnabled;

}
