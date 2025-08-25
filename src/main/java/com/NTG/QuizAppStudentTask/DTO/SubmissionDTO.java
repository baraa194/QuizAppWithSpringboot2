package com.NTG.QuizAppStudentTask.DTO;
import java.time.LocalDateTime;
import java.util.List;

public class SubmissionDTO {
    private int id;
    private LocalDateTime submittedAt;
    private float totalGrade;
    private boolean autoSubmitted;
    private LocalDateTime startedAt;
    private LocalDateTime deadline;
    private String status;
    private int studentId;
    private int quizId;
    private List<SubmissionAnswerDTO> answers;
}
