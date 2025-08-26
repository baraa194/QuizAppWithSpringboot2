package com.NTG.QuizAppStudentTask.Controllers;


import com.NTG.QuizAppStudentTask.DTO.QuizDTO;
import com.NTG.QuizAppStudentTask.DTO.QuizWithquestionsDTO;
import com.NTG.QuizAppStudentTask.Models.Quiz;
import com.NTG.QuizAppStudentTask.Services.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quiz")


public class QuizController {
    @Autowired
private  QuizService quizService;

    @PreAuthorize("hasAnyRole('ADMIN' , 'TEACHER' )")
    @PostMapping("/td/quiz/create")
    public void createQuiz(@RequestBody QuizWithquestionsDTO quiz){

       quizService.createQuizWithQuestions(quiz);
    }

    @PreAuthorize("hasAnyRole('ADMIN' , 'TEACHER' )")
    @PutMapping("/td/quiz/update/{id}")
    public ResponseEntity<QuizDTO> updateQuiz(@PathVariable int id,@RequestBody QuizDTO quiz)
    {
        return new ResponseEntity<>(quizService.updateQuiz(id,quiz), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN' , 'TEACHER' )")
    @DeleteMapping("/td/quiz/delete/{id}")
    public void deleteQuiz(@PathVariable int id)
    {
        quizService.deleteQuiz(id);
    }



    @PreAuthorize("hasAnyRole('ADMIN' , 'TEACHER' , 'STUDENT')")
    @GetMapping("/quiz/getall")
    public ResponseEntity<List<QuizDTO>> getQuizzes(){

        return new ResponseEntity<>(quizService.getAllWithStatus(), HttpStatus.OK);

    }

    @PreAuthorize("hasAnyRole('ADMIN' , 'TEACHER' , 'STUDENT')")
    @GetMapping("/quiz/getbyId/{id}")
    public ResponseEntity<QuizDTO> getQuizById(@PathVariable int id)
    {
        return new ResponseEntity<>(quizService.getById(id), HttpStatus.OK);
    }

    @GetMapping("/quiz/getallquizzesResults")
    @PreAuthorize("hasAnyRole('ADMIN' , 'TEACHER' , 'STUDENT')")
    public ResponseEntity<?>  getAllQuizResults()
    {
        return new ResponseEntity<>(quizService.getAllResults(), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN' , 'TEACHER' )")
    @GetMapping("/td/quiz/getteacherquizById/{quizid}/{teacherid}")
    public ResponseEntity<?> getTeacherQuizById(@PathVariable int id,@PathVariable int teacherid)
    {
        return new ResponseEntity<>(quizService.getteacherQuizById(id,teacherid), HttpStatus.OK);
    }



}
