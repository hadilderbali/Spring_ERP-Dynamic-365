package com.example.businesscentral.Repository;

import com.example.businesscentral.Entity.Ticket;
import com.example.businesscentral.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
        List<Ticket> findByCreatedDateBetween(Date startDate, Date endDate);
        List<Ticket> findByTitleContaining(String name);
        List<Ticket> findByDateSBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT t FROM Ticket t WHERE t.assignedUser = :user AND t.dateS BETWEEN :startDate AND :endDate")
    List<Ticket> findTicketsByAssignedUserAndDateRange(
            @Param("user") User user,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);


    @Query("SELECT t FROM Ticket t WHERE t.createdDate BETWEEN :startDate AND :endDate")
    List<Ticket> findTicketsByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}

