package com.NTG.QuizAppStudentTask.Repositories;

import com.NTG.QuizAppStudentTask.Models.Question;
import com.NTG.QuizAppStudentTask.Models.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface questionRepo extends JpaRepository<Question,Integer> {
    @Query("SELECT q FROM Question q WHERE q.quiz.Id = :quizId")
    List<Question> findByQuizId(@Param("quizId") Integer quizId);

}
