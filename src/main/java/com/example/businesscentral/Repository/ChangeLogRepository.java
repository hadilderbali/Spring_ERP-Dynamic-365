package com.example.businesscentral.Repository;

import com.example.businesscentral.Entity.ChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChangeLogRepository extends JpaRepository<ChangeLog,Long> {
    List<ChangeLog> findByTicketId(Long ticketId);
}
