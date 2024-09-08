package com.example.businesscentral.Repository;

import com.example.businesscentral.Entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
        List<Ticket> findByCreatedDateBetween(Date startDate, Date endDate);
        List<Ticket> findByTitleContaining(String name);
        List<Ticket> findByDateSBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT t FROM Ticket t JOIN t.assignedUser u WHERE u = :user AND t.dateS BETWEEN :startDate AND :endDate")
    List<Ticket> findTicketsByAssignedUserAndDateRange(
            @Param("user") User user,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);


    @Query("SELECT t FROM Ticket t WHERE t.createdDate BETWEEN :startDate AND :endDate")
    List<Ticket> findTicketsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);


    @Query("SELECT DISTINCT t FROM Ticket t " +
            "JOIN t.events e " +

            "WHERE e.name = :eventName ")
    List<Ticket> findByEventNameAndRoleNames(@Param("eventName") String eventName);

    @Query("SELECT t FROM Ticket t JOIN t.assignedUser u WHERE u.userid = :userId")
    List<Ticket> findByAssignedUserId(@Param("userId") Long userId);

    @Query("SELECT t FROM Ticket t JOIN t.assignedUser u WHERE u.userid = :userId AND t.status IN :statuses")
    List<Ticket> findByAssignedUserAndStatusIn(@Param("userId") Long userId, @Param("statuses") List<Status> statuses);

    @Query("SELECT t FROM Ticket t JOIN t.assignedUser u WHERE u.userid = :userId AND t.status = :status")
    List<Ticket> findByAssignedUserAndStatus(@Param("userId") Long userId, @Param("status") Status status);
}

