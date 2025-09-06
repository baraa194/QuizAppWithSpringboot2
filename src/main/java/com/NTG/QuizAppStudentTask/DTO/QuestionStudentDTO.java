package com.NTG.QuizAppStudentTask.DTO;

import com.NTG.QuizAppStudentTask.Models.Option;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionStudentDTO {
    private int id;
    private String text;
    private String type; // "MCQ" | "WRITTEN"
    private List<StudentOptionDTO> options = new ArrayList<>();
}
