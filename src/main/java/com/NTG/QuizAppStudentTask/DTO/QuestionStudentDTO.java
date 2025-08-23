package com.NTG.QuizAppStudentTask.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionStudentDTO {
    private String text;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String questionType;
    private int grade;
}
