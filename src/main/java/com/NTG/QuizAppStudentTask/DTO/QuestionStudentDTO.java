package com.NTG.QuizAppStudentTask.DTO;

import com.NTG.QuizAppStudentTask.Models.Option;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionStudentDTO {
    private String text;
    private List<Option> options;
    private String questionType;
    private int grade;
}
