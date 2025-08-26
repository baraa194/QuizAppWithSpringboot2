package com.NTG.QuizAppStudentTask.DTO;

import com.NTG.QuizAppStudentTask.DTO.QuestionStudentDTO;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class startQuizResponseDTO {
    private int submissionId;
    private LocalDateTime endTime;
    private String quiztitle;
    private List<QuestionStudentDTO> questions;
}
