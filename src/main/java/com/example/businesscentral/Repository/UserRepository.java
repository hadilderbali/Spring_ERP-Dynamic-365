package com.example.businesscentral.Repository;

import com.example.businesscentral.Entity.Event;
import com.example.businesscentral.Entity.Role;
import com.example.businesscentral.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface UserRepository extends JpaRepository<User,Long> {
    /**
     * Finds users by a list of ticket IDs.
     * @param ticketIds List of ticket IDs.
     * @return List of users associated with the given ticket IDs.
     */
    @Query("SELECT DISTINCT u FROM User u " +
            "JOIN u.teams t " +
            "JOIN t.projects pi " +
            "JOIN pi.tickets ti " +
            "WHERE ti.id IN :ticketIds")
    List<User> findUsersByTicketIds(@Param("ticketIds") List<Long> ticketIds);

    /**
     * Finds users by a list of project IDs.
     * @param projectIds List of project IDs.
     * @return List of users associated with the given project IDs.
     */
    @Query("SELECT DISTINCT u FROM User u " +
            "JOIN u.teams t " +
            "JOIN t.projects pi " +
            "WHERE pi.id IN :projectIds")
    List<User> findUsersByProjectIds(@Param("projectIds") List<Long> projectIds);

    /**
     * Finds users by a list of team IDs.
     * @param teamIds List of team IDs.
     * @return List of users associated with the given team IDs.
     */
    @Query("SELECT DISTINCT u FROM User u " +
            "JOIN u.teams t " +
            "WHERE t.id IN :teamIds")
    List<User> findUsersByTeamIds(@Param("teamIds") List<Long> teamIds);


    List<User> findByUsernameContainingIgnoreCase(String name);


    @Query("SELECT DISTINCT u FROM User u " +
            "JOIN u.roles r " +
            "JOIN u.tickets t " +
            "JOIN t.events e " +
            "WHERE r IN :roles AND e = :event")
    List<User> findByRolesAndEvent(@Param("roles") Set<Role> roles, @Param("event") Event event);

    @Query("SELECT DISTINCT u FROM User u " +
            "JOIN u.roles r " +
            "JOIN u.tickets t " +
            "JOIN t.events e " +
            "WHERE r.name IN :roleNames AND e.name = :eventName")
    List<User> findByRoleNamesAndEventName(@Param("roleNames") Set<String> roleNames, @Param("eventName") String eventName);
}

