package com.NTG.QuizAppStudentTask.Repositories;

import com.NTG.QuizAppStudentTask.DTO.SubmissionSummaryDTO;
import com.NTG.QuizAppStudentTask.Models.Quiz;
import com.NTG.QuizAppStudentTask.Models.Submission;
import com.NTG.QuizAppStudentTask.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepo extends JpaRepository<Submission,Integer> {
    boolean existsByStudentAndQuiz(User student, Quiz quiz);

    @Query("SELECT s FROM Submission s WHERE s.deadline <= :now AND s.submittedAt IS NULL")
    List<Submission> findExpiredUnsubmitted(@Param("now") LocalDateTime now);


    Optional<Submission> findByStudentAndQuiz(User student, Quiz quiz);


    @Query("SELECT new com.NTG.QuizAppStudentTask.DTO.SubmissionSummaryDTO(" +
            "sub.Id, st.name, st.Id,sub.totalGrade, sub.startedAt, sub.submittedAt) " +
            "FROM Submission sub " +
            "JOIN sub.student st " +
            "WHERE sub.quiz.Id = :quizId")
    List<SubmissionSummaryDTO> findSubmissionsByQuizId(@Param("quizId") int quizId);
}
