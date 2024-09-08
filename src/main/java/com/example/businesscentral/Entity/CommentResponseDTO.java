package com.example.businesscentral.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDTO {

    private Long commentId;
    private String content;
    private String authorUsername;
    private LocalDateTime createdDate;
    private Long ticketId;
    private String ticketTitle;

}
