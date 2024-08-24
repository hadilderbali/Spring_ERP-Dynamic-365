package com.example.businesscentral.Repository;

import com.example.businesscentral.Entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {

    List<Team> findByNameTContaining(String name);

}
