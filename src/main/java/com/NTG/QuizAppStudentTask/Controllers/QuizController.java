package com.NTG.QuizAppStudentTask.Controllers;


import com.NTG.QuizAppStudentTask.DTO.QuizDTO;
import com.NTG.QuizAppStudentTask.Models.Quiz;
import com.NTG.QuizAppStudentTask.Services.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

}
