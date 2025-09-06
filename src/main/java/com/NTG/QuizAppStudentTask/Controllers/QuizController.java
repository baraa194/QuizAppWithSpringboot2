package com.NTG.QuizAppStudentTask.Controllers;


import com.NTG.QuizAppStudentTask.DTO.QuizDTO;
import com.NTG.QuizAppStudentTask.DTO.QuizWithquestionsDTO;
import com.NTG.QuizAppStudentTask.Models.Quiz;
import com.NTG.QuizAppStudentTask.Services.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    @PostMapping(
            value = "/td/quiz/create",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public void createQuiz(@RequestBody QuizWithquestionsDTO quiz){

       quizService.createQuizWithQuestions(quiz);
    }

   @PreAuthorize("hasAnyRole('ADMIN' , 'TEACHER' )")
   @PutMapping("/td/quiz/update/{id}")
   public void updateQuiz(@PathVariable int id,@RequestBody QuizWithquestionsDTO quiz)
   {
      quizService.updateQuizWithQuestions(id,quiz);
 }

    @PreAuthorize("hasAnyRole('ADMIN' , 'TEACHER' )")
    @DeleteMapping("/td/quiz/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        quizService.deleteQuiz(id);
        return ResponseEntity.noContent().build();
    }



   @PreAuthorize("hasAnyRole('ADMIN' , 'TEACHER' , 'STUDENT')")
    @GetMapping("/quiz/getall")
    public ResponseEntity<List<QuizDTO>> getQuizzes(){

        return new ResponseEntity<>(quizService.getAllForStudents(), HttpStatus.OK);

    }

    /*@PreAuthorize("hasAnyRole('ADMIN' , 'TEACHER' , 'STUDENT')")
    @GetMapping("/quiz/getbyId/{id}")
    public ResponseEntity<QuizDTO> getQuizById(@PathVariable int id)
    {
        return new ResponseEntity<>(quizService.getById(id), HttpStatus.OK);
    }*/

    @PreAuthorize("hasAnyRole('ADMIN' , 'TEACHER' )")
    @GetMapping("/quiz/getallteacherQuizzes")
    public ResponseEntity<List<QuizDTO>> getAllTeacherQuizzes(){
        return new ResponseEntity<>(quizService.getQuizzesForCurrentTeacher(),HttpStatus.OK);

    }



    @GetMapping("/quiz/getallquizzesResults")
    @PreAuthorize("hasAnyRole('ADMIN' , 'TEACHER' , 'STUDENT')")
    public ResponseEntity<?>  getAllQuizResults()
    {
        return new ResponseEntity<>(quizService.getAllResults(), HttpStatus.OK);
    }

    @GetMapping("/td/quiz/{id}")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<QuizWithquestionsDTO> getquizWithquestionsByid(@PathVariable int id) {
        return ResponseEntity.ok(quizService.getQuizWithQuestionsById(id));
    }


   @PostMapping("/quiz/{id}/unpublish")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public void unpublishQuiz(@PathVariable int id)
    {
        quizService.unpublishQuiz(id);
    }
    @PostMapping("/quiz/{id}/publish")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public void publishQuiz(@PathVariable int id)
    {
        quizService.publishQuiz(id);
    }

}
