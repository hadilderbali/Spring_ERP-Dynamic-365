package com.example.businesscentral.Controller;


import com.example.businesscentral.Entity.BadWord;
import com.example.businesscentral.Entity.Comment;
import com.example.businesscentral.Entity.CommentDTO;
import com.example.businesscentral.Entity.CommentResponseDTO;
import com.example.businesscentral.Repository.BadWordRepository;
import com.example.businesscentral.Repository.CommentRepository;
import com.example.businesscentral.Service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/comment")
@CrossOrigin("http://localhost:4200")
public class CommentController {
    final    private CommentService commentService;
    private final BadWordRepository badWordRepository;

    private final CommentRepository commentRepository;

    @GetMapping("/listbadwords")
    public ResponseEntity<List<BadWord>> listBadWords() {
        List<BadWord> badWords = badWordRepository.findAll();
        return ResponseEntity.ok(badWords);
    }

    @PostMapping("/tickets/{ticketId}/users/{userId}/comments")
    public ResponseEntity<CommentDTO> addCommentToTicket(
            @PathVariable Long ticketId,
            @PathVariable Long userId,
            @RequestBody Comment comment) {

        try {
            CommentDTO commentDTO = commentService.addComment(ticketId, userId, comment);
            return ResponseEntity.ok(commentDTO);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(null);
        }
    }

    @GetMapping("/tickets/{ticketId}/comments")
    public ResponseEntity<List<CommentDTO>> getCommentsForTicket(@PathVariable Long ticketId) {
        List<Comment> comments = commentRepository.findByTicketId(ticketId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        List<CommentDTO> commentDTOs = comments.stream().map(comment -> {
            return new CommentDTO(
                    comment.getId(),
                    comment.getContent(),
                    comment.getCreatedDate().format(formatter), // Format LocalDateTime to String
                    comment.getAuthor().getUserid(), // Use `getUserid()` if that's your field name
                    comment.getAuthor().getUsername(),
                    comment.getTicket().getId() // Assuming `getTicket()` is available and has an `Id`
            );
        }).collect(Collectors.toList());

        return ResponseEntity.ok(commentDTOs);
    }

    @GetMapping("/grouped-by-week")
    public Map<String, List<CommentResponseDTO>> getCommentsGroupedByWeek() {
        return commentService.getCommentsGroupedByWeek();
    }
}
