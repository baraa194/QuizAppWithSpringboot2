package com.NTG.QuizAppStudentTask.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SubmissionRequest {

    private int studentId;
    private int quizId;
    private List<SubmissionAnswerRequest> answers;

}
