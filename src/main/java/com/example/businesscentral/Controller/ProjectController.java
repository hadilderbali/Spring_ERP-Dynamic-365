package com.example.businesscentral.Controller;

import com.example.businesscentral.Entity.Project;
import com.example.businesscentral.Entity.Team;
import com.example.businesscentral.Entity.Ticket;
import com.example.businesscentral.Service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/Project")

public class ProjectController {
    private  final ProjectService projectService;
    @PostMapping("/createProject")

    public Project createProject ( @RequestBody Project project) {
        return  projectService.createProject(project);

    }

    @PostMapping("/{projectId}/assignTeam")
    public ResponseEntity<Project> assignProjectToTeam(
            @PathVariable Long projectId,
            @RequestParam Long teamId) {
        Project updatedProject = projectService.assignProjectToTeam(projectId, teamId);
        return ResponseEntity.ok(updatedProject);
    }
    @PostMapping("/team")
    public Team createTeam(@RequestBody Team team){
        return  projectService.createTeam(team);
    }
    }
