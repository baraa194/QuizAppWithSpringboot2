package com.NTG.QuizAppStudentTask.Repositories;

import com.NTG.QuizAppStudentTask.Models.StudentTeacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StudentTeacherRepo extends JpaRepository<StudentTeacher,Long>{
    List<StudentTeacher> findByStudent_Id(int studentId);
    List<StudentTeacher> findByTeacher_Id(int teacherId);
}
