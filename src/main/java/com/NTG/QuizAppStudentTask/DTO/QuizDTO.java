package com.NTG.QuizAppStudentTask.DTO;

import com.NTG.QuizAppStudentTask.Models.Quiz;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
//@AllArgsConstructor
@NoArgsConstructor
public class QuizDTO {


    private String title ;
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime endTime ;
    private   String status;
    private Long remainingTime;
    private int createdByUserId;


    public QuizDTO(int id, String title, String description, LocalDateTime startTime,
                   LocalDateTime endTime, String status, int createdByUserId) {

        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.remainingTime = Duration.between(LocalDateTime.now(), endTime).toMinutes();
        this.createdByUserId = createdByUserId;
    }






}
