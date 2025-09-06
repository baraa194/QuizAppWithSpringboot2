package com.NTG.QuizAppStudentTask.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentTeacherDTO {
    long id;
    int studentId;
    int teacherId;
    LocalDate createdTime;
}
