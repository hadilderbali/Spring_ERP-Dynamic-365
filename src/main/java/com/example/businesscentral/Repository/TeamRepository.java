package com.example.businesscentral.Repository;

import com.example.businesscentral.Entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
