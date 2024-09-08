package com.example.businesscentral.Service;

import com.example.businesscentral.Entity.*;
import com.example.businesscentral.Repository.ProjectRepository;
import com.example.businesscentral.Repository.TeamRepository;
import com.example.businesscentral.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class ProjectService {
    final ProjectRepository projectInterface;
   final TeamRepository teamInterface;
   final UserRepository userRepository;
    public Project createProject(Project project) {
        return projectInterface.save(project);
    }
    public Project assignProjectToTeam(Long projectId, Long teamId) {
        Optional<Project> optionalProject = projectInterface.findById(projectId);
        Optional<Team> optionalTeam = teamInterface.findById(teamId);

        if (optionalProject.isPresent() && optionalTeam.isPresent()) {
            Project project = optionalProject.get();
            Team team = optionalTeam.get();
            project.getTeams().add(team);
            return projectInterface.save(project);
        } else {
            throw new RuntimeException("Project or Team not found");
        }
    }
public  User createUser(User user){
        return  userRepository.save(user);
}
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    public Team createTeam(Team team, Set<Long> userIds) {
        // Save the team first
        Team savedTeam = teamInterface.save(team);

        // Assign users to the team
        if (userIds != null && !userIds.isEmpty()) {
            for (Long userId : userIds) {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new EntityNotFoundException("User not found with id " + userId));
                savedTeam.getUsers().add(user);
                user.getTeams().add(savedTeam);
            }
            // Save the team again with the assigned users
            savedTeam = teamInterface.save(savedTeam);
        }

        return savedTeam;
    }
public List<Team> GetAllTeams( ){
        return teamInterface.findAll();
}


public List<Project> GetAllProjects(){
        return  projectInterface.findAll();
}

public Project GetProject(Long projectId){
        return projectInterface.findById(projectId).orElse(null);
}
public  Team GetTeam(Long teamId){
        return  teamInterface.findById(teamId).orElse(null);
}

    public List<User> getUsersByRoleNamesAndEventName(Set<String> roleNames, String eventName) {
        return userRepository.findByRoleNamesAndEventName(roleNames, eventName);
    }
    public User getUser (Long id){
        return  userRepository.findById(id).orElse(null);
    }
    public List<User> findUsersByName(String name) {
        return userRepository.findByUsernameContainingIgnoreCase(name);
    }
}



