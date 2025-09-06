package com.NTG.QuizAppStudentTask.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OptionDTO {
    @JsonProperty("correct")

    private boolean isCorrect;
    private String answer;
}
