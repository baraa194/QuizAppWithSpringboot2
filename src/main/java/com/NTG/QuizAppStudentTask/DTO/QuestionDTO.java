package com.NTG.QuizAppStudentTask.DTO;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class QuestionDTO {
   // private int Id;
    private String  text ;
    private List<OptionDTO> options;
    private String type; // MCQ / TrueFalse / Written
    private int Grade ;

}
