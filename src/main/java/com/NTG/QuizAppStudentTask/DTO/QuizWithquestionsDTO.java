package com.NTG.QuizAppStudentTask.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class QuizWithquestionsDTO {

        private String title;
        private String description;
        private LocalDateTime startTime;
        private Long duration;
        private String status;
        private String modelAnswer;
        private List<QuestionDTO> questions;


}
