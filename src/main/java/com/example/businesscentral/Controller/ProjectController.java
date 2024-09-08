package com.example.businesscentral.Controller;

import com.example.businesscentral.Entity.*;
import com.example.businesscentral.Service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/Project")
@CrossOrigin("http://localhost:4200")

public class ProjectController {
    private final ProjectService projectService;

    @PostMapping("/createProject")
    public ResponseEntity<Project> createProject(@RequestBody Project project) {
        try {
            Project createdProject = projectService.createProject(project);
            return new ResponseEntity<>(createdProject, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("/{projectId}/assignTeam")
    public ResponseEntity<Project> assignProjectToTeam(
            @PathVariable Long projectId,
            @RequestParam Long teamId) {
        Project updatedProject = projectService.assignProjectToTeam(projectId, teamId);
        return ResponseEntity.ok(updatedProject);
    }

    @PostMapping("/team")
    public Team createTeam(@RequestBody TeamCreationDTO teamCreationDTO) {
        Team team = new Team();
        team.setNameT(teamCreationDTO.getNameT());
        team.setMembreInteger(teamCreationDTO.getMembreInteger());

        return projectService.createTeam(team, teamCreationDTO.getUserIds());
    }



    @GetMapping("/Teams")
    public List<Team> GetAllTeams() {
        return projectService.GetAllTeams();
    }

    @GetMapping("/project/{id}")
    public Project GetProject(@PathVariable Long id) {
        return projectService.GetProject(id);
    }

    @GetMapping("/getProject")
    public List<Project> GetAllProjects() {
        return projectService.GetAllProjects();
    }

    @GetMapping("/getTeam/{id}")
    public Team GetTeam(@PathVariable Long id) {
        return projectService.GetTeam(id);
    }

    @GetMapping("/getUsersByEventAndRoles")
    public List<User> getUsersByEventNameAndRoleNames(
            @RequestParam("eventName") String eventName,
            @RequestParam("roleNames") Set<String> roleNames
    ) {
        return projectService.getUsersByRoleNamesAndEventName(roleNames, eventName);
    }

 @GetMapping("/getUser/{id}")
    public User getUser(@PathVariable Long id){
        return projectService.getUser(id);
 }

    @GetMapping("/user")
    public List<User> getAllUsers() {
        return projectService.getAllUsers();
    }
    }

