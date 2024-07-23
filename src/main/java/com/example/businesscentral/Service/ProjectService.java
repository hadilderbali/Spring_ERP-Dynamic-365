package com.example.businesscentral.Service;

import com.example.businesscentral.Entity.Project;
import com.example.businesscentral.Entity.Team;
import com.example.businesscentral.Repository.ProjectRepository;
import com.example.businesscentral.Repository.TeamRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ProjectService {
    final ProjectRepository projectInterface;
   final TeamRepository teamInterface;
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
public  Team createTeam(Team team){
        return  teamInterface.save(team);
}


}