package com.NTG.QuizAppStudentTask.DTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class StartQuizRequestDTO {
    private int quizId;
    private int studentId;

}
