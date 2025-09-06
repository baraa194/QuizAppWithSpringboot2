package com.NTG.QuizAppStudentTask.Controllers;

import com.NTG.QuizAppStudentTask.DTO.QuestionDTO;
import com.NTG.QuizAppStudentTask.Models.Question;
import com.NTG.QuizAppStudentTask.Repositories.QuizRepo;
import com.NTG.QuizAppStudentTask.Services.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

public class QuestionController {
    @Autowired
    private QuestionService questionService;

    @PreAuthorize("hasAnyRole( 'STUDENT','ADMIN','TEACHER')")
    @GetMapping("/quiz/questions/getbyquizid/{id}")
    public ResponseEntity<List<QuestionDTO>> getAllQuestions(@PathVariable int id)
    {
        return new ResponseEntity<>(questionService.findQuesByquiz(id), HttpStatus.OK);
    }

    //add question
    @PreAuthorize("hasAnyRole( 'ADMIN','TEACHER')")
    @PostMapping("/td/quiz/{quizId}/addQuestion")
    public ResponseEntity<QuestionDTO> addQuestion(@PathVariable int quizId, @RequestBody QuestionDTO questionDTO)
    {
        return new ResponseEntity<>(questionService.addQuestion(quizId,questionDTO), HttpStatus.OK);
    }

    //update question
    @PreAuthorize("hasAnyRole( 'ADMIN','TEACHER')")
    @PutMapping("/td/quiz/{quizId}/update/{questionId}")
    public QuestionDTO updateQuestionInQuiz(@PathVariable int quizId, @PathVariable int questionId, @RequestBody QuestionDTO dto) {
        return questionService.updateQuestionInQuiz(quizId, questionId, dto);
    }

    //delete question
    @PreAuthorize("hasAnyRole( 'ADMIN','TEACHER')")
    @DeleteMapping("/td/quiz/{quizId}/delete/{questionId}")
    public ResponseEntity<String> deleteQuestion(@PathVariable int quizId, @PathVariable int questionId){
        questionService.deleteQuestion(quizId,questionId);
        return ResponseEntity.ok("question deleted successfully");

    }






}
