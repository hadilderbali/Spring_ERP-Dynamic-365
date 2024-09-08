package com.example.businesscentral.Service;

import com.example.businesscentral.Entity.*;
import com.example.businesscentral.Repository.BadWordRepository;
import com.example.businesscentral.Repository.CommentRepository;
import com.example.businesscentral.Repository.TicketRepository;
import com.example.businesscentral.Repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor

@Service
public class CommentService {

    private final  CommentRepository commentRepository;

    private BadWordRepository badWordRepository;
    private final TicketRepository ticketRepository;


    private final UserRepository userRepository;

    public CommentDTO addComment(Long ticketId, Long userId, Comment comment) {
        List<BadWord> badWords = badWordRepository.findAll();
        String commentContent = comment.getContent();

        // Check and filter bad words
        if (containsBadWord(commentContent, badWords)) {
            // Replace bad words with asterisks
            commentContent = replaceBadWords(commentContent, badWords);

            // If comment content is empty after filtering, do not add the comment
            if (commentContent.trim().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comment content cannot be empty after filtering.");
            }

            // Update comment content with filtered words
            comment.setContent(commentContent);
        }

        // Retrieve the associated ticket
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found with id " + ticketId));

        // Retrieve the associated user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id " + userId));

        // Associate the comment with the ticket and user
        comment.setTicket(ticket);
        comment.setAuthor(user);

        // Save the comment in the database
        Comment savedComment = commentRepository.save(comment);

        // Convert the saved Comment entity to a CommentDTO
        return new CommentDTO(
                savedComment.getId(),
                savedComment.getContent(),
                savedComment.getCreatedDate().toString(),
                savedComment.getAuthor().getUserid(),
                savedComment.getAuthor().getUsername(), // Assuming you have a username field in the User entity
                savedComment.getTicket().getId()
        );
    }


    private boolean containsBadWord(String content, List<BadWord> badWords) {
        for (BadWord badWord : badWords) {
            if (content.toLowerCase().contains(badWord.getWord().toLowerCase())) {
                return true; // Bad word found
            }
        }
        return false; // No bad word found
    }

    private String replaceBadWords(String content, List<BadWord> badWords) {
        for (BadWord badWord : badWords) {
            String regex = "\\b" + badWord.getWord() + "\\b";
            content = content.replaceAll(regex, "*".repeat(badWord.getWord().length()));
        }
        return content;
    }

    public Map<String, List<CommentResponseDTO>> getCommentsGroupedByWeek() {
        List<Object[]> rawComments = commentRepository.findAllCommentsWithTicketDetails();

        List<CommentResponseDTO> comments = rawComments.stream().map(objects -> {
            Long commentId = (Long) objects[0];
            String content = (String) objects[1];
            String authorUsername = (String) objects[2];
            LocalDateTime createdDate = (LocalDateTime) objects[3];
            Long ticketId = (Long) objects[4];
            String ticketTitle = (String) objects[5];

            return new CommentResponseDTO(commentId, content, authorUsername, createdDate, ticketId, ticketTitle);
        }).collect(Collectors.toList());

        // Group by the week of creation
        return comments.stream()
                .collect(Collectors.groupingBy(comment -> {
                    LocalDateTime date = comment.getCreatedDate();
                    LocalDate firstDayOfWeek = date.toLocalDate().with(java.time.temporal.WeekFields.of(java.util.Locale.getDefault()).dayOfWeek(), 1);
                    return firstDayOfWeek.toString();
                }));
    }

}
