package com.NTG.QuizAppStudentTask.Controllers;

import com.NTG.QuizAppStudentTask.DTO.QuestionDTO;
import com.NTG.QuizAppStudentTask.Models.Question;
import com.NTG.QuizAppStudentTask.Repositories.QuizRepo;
import com.NTG.QuizAppStudentTask.Services.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/Questions")
public class QuestionController {
    @Autowired
    private QuestionService questionService;

    @GetMapping("{id}")
    public ResponseEntity<List<QuestionDTO>> getAllQuestions(int id)
    {
        return new ResponseEntity<>(questionService.findQuesByquiz(id), HttpStatus.OK);
    }


}
