package com.NTG.QuizAppStudentTask.DTO;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class SubmitAnswerDTO {

    private int Id;
    private String studentAnswer ;
    private boolean isCorrect ;
    private float manualGrade ;
}
