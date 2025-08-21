package com.NTG.QuizAppStudentTask.DTO;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class startQuizResponseDTO {
    private int submissionId;
    private LocalDateTime endTime;
    private String quiztitle;
}
