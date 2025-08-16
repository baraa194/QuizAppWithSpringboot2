package com.NTG.QuizAppStudentTask.DTO;

import com.NTG.QuizAppStudentTask.Models.Quiz;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class QuizDTO {

    private int Id;
    private String title ;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime ;
    private String status;
    private Long remainingTime;
    public QuizDTO(int id, String title, String description, LocalDateTime startTime,
                   LocalDateTime endTime, String status) {
        this(id, title, description, startTime, endTime, status, null);
    }

    public QuizDTO(int id, String title, String description, LocalDateTime startTime,
                   LocalDateTime endTime, String status, Long remainingTime) {
        this.Id = id;
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.remainingTime = remainingTime;
    }



}
