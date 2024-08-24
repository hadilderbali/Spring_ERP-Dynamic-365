package com.example.businesscentral.Controller;

import com.example.businesscentral.Entity.*;
import com.example.businesscentral.Service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/Project")
@CrossOrigin("http://localhost:4200")

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
    public Team createTeam(@RequestBody TeamCreationDTO teamCreationDTO) {
        Team team = new Team();
        team.setNameT(teamCreationDTO.getNameT());
        team.setMembreInteger(teamCreationDTO.getMembreInteger());

        return projectService.createTeam(team, teamCreationDTO.getUserIds());
    }


    @PostMapping("/user")
    public User createUser(@RequestBody User user){
        return  projectService.createUser(user);
    }

    @GetMapping("/Teams")
    public List<Team> GetAllTeams(){
        return  projectService.GetAllTeams();
    }

    @GetMapping("/project/{id}")
    public Project GetProject(@PathVariable Long id){
        return  projectService.GetProject(id);
    }
@GetMapping("/getProject")
    public List<Project>  GetAllProjects(){
        return projectService.GetAllProjects();
}
@GetMapping("/getTeam/{id}")
    public  Team GetTeam(@PathVariable Long id){
        return projectService.GetTeam(id);
}
}
