package com.NTG.QuizAppStudentTask.DTO;

import com.NTG.QuizAppStudentTask.Models.Quiz;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class QuizWithquestionsDTO {

        private String title;
        private String description;

        // توحيد النوع مع QuizDTO
        @JsonFormat(shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", // ✅ أضف SSS
                timezone = "UTC")
        private OffsetDateTime startTimeUtc;

        private Long durationMinutes;

        private String status;
        private List<QuestionDTO> questions;
        private String modelAnswer;

        // اختياري: مخرج محسوب للنهاية
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "UTC")
        public OffsetDateTime getEndTimeUtc() {
                return startTimeUtc.plusMinutes(durationMinutes == null ? 0 : durationMinutes);
        }
}