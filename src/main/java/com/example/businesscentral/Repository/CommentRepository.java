package com.example.businesscentral.Repository;

import com.example.businesscentral.Entity.Comment;
import com.example.businesscentral.Entity.CommentResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c JOIN c.author a WHERE c.ticket.id = :ticketId")
    List<Comment> findByTicketId(Long ticketId);

    @Query("SELECT c.id, c.content, u.username, c.createdDate, t.id, t.title " +
            "FROM Comment c " +
            "JOIN c.ticket t " +
            "JOIN c.author u " +
            "ORDER BY c.createdDate DESC")
    List<Object[]> findAllCommentsWithTicketDetails();


}