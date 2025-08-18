package com.NTG.QuizAppStudentTask.Mapper;
import com.NTG.QuizAppStudentTask.Models.Quiz;
import com.NTG.QuizAppStudentTask.DTO.QuizDTO;
public class  QuizMapper {

    public static QuizDTO toDTO(Quiz quiz){
        QuizDTO dto = new QuizDTO();
        dto.setTitle(quiz.getTitle());
        dto.setDescription(quiz.getDescription());
        dto.setStartTime( quiz.getStartTime());
        dto.setEndTime(quiz.getEndTime());
        dto.setStatus(quiz.getStatus().name());
        return dto;
    }
    public static Quiz toQuiz(QuizDTO quizDTO){
        Quiz quiz = new Quiz();
        quiz.setTitle(quizDTO.getTitle());
        quiz.setDescription(quizDTO.getDescription());
        quiz.setStartTime( quizDTO.getStartTime());
        quiz.setEndTime(quizDTO.getEndTime());
        quiz.setStatus(Quiz.Status.valueOf(quizDTO.getStatus()));
        return quiz;
    }
}
