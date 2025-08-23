package com.NTG.QuizAppStudentTask.Repositories;

import com.NTG.QuizAppStudentTask.DTO.QuizDTO;
import com.NTG.QuizAppStudentTask.DTO.QuizResultDTO;
import com.NTG.QuizAppStudentTask.Models.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepo extends JpaRepository<Quiz,Integer> {

    @Query("SELECT new com.NTG.QuizAppStudentTask.DTO.QuizResultDTO(" +
            "q.Id, q.title, st.Id, st.name, sub.totalGrade, sub.submittedAt) " +
            "FROM Submission sub " +
            "JOIN sub.quiz q " +
            "JOIN sub.student st " +
            "WHERE sub.status = com.NTG.QuizAppStudentTask.Models.Submission.Status.COMPLETED " +
            "AND st.role.Id = 2")
    List<QuizResultDTO> findStudentQuizResults();


    @Query("SELECT new com.NTG.QuizAppStudentTask.DTO.QuizDTO(" +
            "q.Id, q.title, q.description, q.startTime, q.endTime, CAST(q.status AS string), t.Id) " +
            "FROM Quiz q JOIN q.createdByUser t " +
            "WHERE q.Id = :quizId AND t.Id = :teacherId AND t.role.Id = 3")
    Optional<QuizDTO> findByIdAndTeacherId(@Param("quizId") int quizId, @Param("teacherId") int teacherId);





}
