package com.NTG.QuizAppStudentTask.DTO;

import com.NTG.QuizAppStudentTask.Models.Quiz;
import com.NTG.QuizAppStudentTask.Models.User;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

@Getter
@Setter

public class QuizDTO {

    private int Id;
    private String title ;
    private String description;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime startTime;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime endTime ;
    private String status;
    private Long remainingTime;
    private int createdByUserId;

    public QuizDTO() {}
    public QuizDTO( String title, String description, LocalDateTime startTime,
                   LocalDateTime endTime, String status) {
        this(title, description, startTime, endTime, status, null);
    }
    public QuizDTO(String title, String description, LocalDateTime startTime,
                   LocalDateTime endTime, String status, int createdByUserId) {
        this(title, description, startTime, endTime, status, null);
        this.createdByUserId=createdByUserId;
    }

    public QuizDTO(String title, String description, LocalDateTime startTime,
                   LocalDateTime endTime, String status, Long remainingTime) {
       // this.Id = id;
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.remainingTime = remainingTime;
    }



}
