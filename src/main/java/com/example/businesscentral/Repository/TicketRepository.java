package com.example.businesscentral.Repository;

import com.example.businesscentral.Entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
        List<Ticket> findByCreatedDateBetween(Date startDate, Date endDate);

}
