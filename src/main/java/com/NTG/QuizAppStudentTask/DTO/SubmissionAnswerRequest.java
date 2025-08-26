package com.NTG.QuizAppStudentTask.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class SubmissionAnswerRequest {

      private int questionId;
      private int selectedOptionId;
      private String studentAnswer;

}
