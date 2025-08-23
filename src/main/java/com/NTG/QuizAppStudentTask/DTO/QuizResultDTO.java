package com.NTG.QuizAppStudentTask.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
public class QuizResultDTO {
    private int quizId;
    private String title ;
    private int studentId;
    private String studentName;
    private float score;
    private LocalDateTime submittedAt;




}
