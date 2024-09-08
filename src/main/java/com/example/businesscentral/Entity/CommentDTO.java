package com.example.businesscentral.Entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentDTO {
    private Long id;
    private String content;
    private String createdDate;
    private Long authorId;
    private String authorName; // Optional: Add more fields if needed
    private Long ticketId;
    public CommentDTO(Long id, String content, String createdDate, Long authorId, String authorUsername, Long ticketId) {
        this.id = id;
        this.content = content;
        this.createdDate = createdDate;
        this.authorId = authorId;
        this.authorName = authorUsername;
        this.ticketId = ticketId;
    }
    public CommentDTO() {}

    }


