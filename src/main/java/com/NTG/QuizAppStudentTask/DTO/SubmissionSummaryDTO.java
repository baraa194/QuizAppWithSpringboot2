package com.NTG.QuizAppStudentTask.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class SubmissionSummaryDTO {
    private int studentId;
    private String StudentName;
    private int submissionId;
    private float totalGrade;
    private LocalDateTime startedAt;
    private LocalDateTime submittedAt;

}
