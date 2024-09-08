package com.example.businesscentral.Repository;

import com.example.businesscentral.Entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findByNameIn(Set<String> roleNames);
}
