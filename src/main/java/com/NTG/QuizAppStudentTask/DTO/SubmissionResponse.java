package com.NTG.QuizAppStudentTask.DTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SubmissionResponse {
    private int studentId;
    private int submissionId;
    private int quizId;
    private String quizTitle;
    private float totalGrade;
    private List<submissionAnswerResponse> answers;
}
