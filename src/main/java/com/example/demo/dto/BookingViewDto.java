package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BookingViewDto {

    private String id;
    private String eventTitle;
    private LocalDateTime eventDateTime;
    private String hallName;
    private String comment;
    private Integer seatsCount;
    private LocalDateTime createdAt;
    private String userFullName;
    private String userEmail;

    public BookingViewDto(String id, String eventTitle, LocalDateTime eventDateTime,
                          String hallName, String comment, Integer seatsCount,
                          LocalDateTime createdAt, String userFullName, String userEmail) {
        this.id = id;
        this.eventTitle = eventTitle;
        this.eventDateTime = eventDateTime;
        this.hallName = hallName;
        this.comment = (comment == null || comment.trim().isEmpty()) ? "Без комментария" : comment;
        this.seatsCount = seatsCount;
        this.createdAt = createdAt;
        this.userFullName = userFullName;
        this.userEmail = userEmail;
    }
}