package com.NTG.QuizAppStudentTask.Repositories;

import com.NTG.QuizAppStudentTask.Models.SubmissionAnswer;
import com.NTG.QuizAppStudentTask.Models.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository

public interface SubmissionAnswerRepo extends JpaRepository<SubmissionAnswer,Integer> {
    Optional<SubmissionAnswer> findBySubmissionAndQuestionId(Submission submission, Long questionId);
}
