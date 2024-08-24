package com.example.businesscentral.Service;

import com.example.businesscentral.Entity.*;
import com.example.businesscentral.Repository.ProjectRepository;
import com.example.businesscentral.Repository.TeamRepository;
import com.example.businesscentral.Repository.TicketRepository;
import com.example.businesscentral.Repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class SearchService {
    private final TicketRepository ticketRepository;
    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;
private  final UserRepository userRepository;
    public UnifiedSearchResponse unifiedSearch(String name) {
        List<Ticket> tickets = ticketRepository.findByTitleContaining(name);
        List<Project> projects = projectRepository.findByNamePContaining(name);
        List<Team> teams = teamRepository.findByNameTContaining(name);

        return new UnifiedSearchResponse(tickets, projects, teams);
    }
    public List<Team> SearchTeam(String name){
        return  teamRepository.findByNameTContaining(name);
    }
    public  List<User> findUsers(String name){
        return userRepository.findByUsernameContainingIgnoreCase(name);
    }
}

