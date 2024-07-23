package com.example.businesscentral.Repository;

import com.example.businesscentral.Entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {

}
