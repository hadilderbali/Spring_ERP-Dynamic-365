package com.example.businesscentral.Entity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UnifiedSearchResponse {
    private List<Ticket> tickets;
    private List<Project> projects;
    private List<Team> teams;}

