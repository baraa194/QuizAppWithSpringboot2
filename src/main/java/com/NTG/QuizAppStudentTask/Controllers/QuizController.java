package com.NTG.QuizAppStudentTask.Controllers;


import com.NTG.QuizAppStudentTask.DTO.QuizDTO;
import com.NTG.QuizAppStudentTask.Models.Quiz;
import com.NTG.QuizAppStudentTask.Services.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quiz")



public class QuizController {
    @Autowired
private  QuizService quizService;
    @GetMapping
    public ResponseEntity<List<QuizDTO>> getQuizzes(){

        return new ResponseEntity<>(quizService.getAllWithStatus(), HttpStatus.OK);

    }

    @GetMapping("/getbyId/{id}")
    public ResponseEntity<QuizDTO> getQuizById(@RequestParam int id)
    {
        return new ResponseEntity<>(quizService.getById(id), HttpStatus.OK);
    }

    //create Quiz
    @PostMapping("/create")
    public ResponseEntity<QuizDTO> getQuizById(@RequestBody QuizDTO quiz)
    {
        return new ResponseEntity<>(quizService.createQuiz(quiz), HttpStatus.OK);
    }
    //get quizes of teacher
    @GetMapping("/TeacherQuizes/{id}")
    public ResponseEntity<List<QuizDTO>> getAllQuizes(@PathVariable int id){
        return new ResponseEntity<>(quizService.getTeacherQuizzes(id), HttpStatus.OK);
    }

    //update on quiz
    @PutMapping("/updateQuiz/{id}")
    public ResponseEntity<QuizDTO> updateQuiz(@PathVariable int id,@RequestBody QuizDTO quizDTO){
        return new ResponseEntity<>(quizService.updateQuiz(id,quizDTO), HttpStatus.OK);
    }

    //delete quiz from teacher
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteQuiz(@PathVariable int id) {
        quizService.deleteQuiz(id);
        return ResponseEntity.ok("Quiz deleted successfully");
    }

    //publish quiz
    @PutMapping("/publish/{id}")
    public ResponseEntity<String>  publishQuiz(@PathVariable int id){
        quizService.publishQuiz(id);
        return ResponseEntity.ok("Quiz published successfully");
    }



}
