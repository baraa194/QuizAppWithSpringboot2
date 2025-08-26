package com.NTG.QuizAppStudentTask.DTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SubmissionAnswerResponse {

        private int questionId;
        private String studentAnswer;
        private boolean isCorrect;
        private String questionText;
        private float grade;


}
