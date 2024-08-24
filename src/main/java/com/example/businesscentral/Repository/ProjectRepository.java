package com.example.businesscentral.Repository;

import com.example.businesscentral.Entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByNamePContaining(String name);

}
