package com.NTG.QuizAppStudentTask.Controllers;

import com.NTG.QuizAppStudentTask.DTO.StartQuizRequestDTO;
import com.NTG.QuizAppStudentTask.DTO.SubmissionRequest;
import com.NTG.QuizAppStudentTask.DTO.startQuizResponseDTO;
import com.NTG.QuizAppStudentTask.Models.Submission;
import com.NTG.QuizAppStudentTask.Services.QuizAttemptService;
import com.NTG.QuizAppStudentTask.Services.SubmissionResultService;
import com.NTG.QuizAppStudentTask.Services.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class SubmissionController {

    @Autowired
    private SubmissionService subService;
    @Autowired
    private  QuizAttemptService attemptService;
    @Autowired
    private SubmissionResultService resultService;

    @PreAuthorize("hasRole( 'STUDENT')")
    @PostMapping("student/startSubmission")

    public startQuizResponseDTO start(@RequestBody StartQuizRequestDTO req)
    { return attemptService.startQuiz(req); }

    @PreAuthorize("hasRole( 'STUDENT')")
    @PostMapping("student/saveSubmission")
    public ResponseEntity<?> save(@RequestBody SubmissionRequest submissionReq) {
        subService.saveSubmission(submissionReq);
        return ResponseEntity.ok("Submission saved and graded successfully!");
    }


    @PreAuthorize("hasAnyRole('ADMIN' , 'TEACHER' , 'STUDENT')")
    @GetMapping("quiz/submissionresult/{id}")

    public ResponseEntity<?> getsubmissions(@PathVariable int id)
    {
        return new ResponseEntity<>(resultService.getSubmissionResult(id), HttpStatus.OK);
    }


    @PreAuthorize("hasAnyRole('ADMIN' , 'TEACHER' )")
    @GetMapping("td/submission/allbyQuizId/{id}")
    public ResponseEntity<?>findAllSubmissionsWithQuizID(@PathVariable int id)
    {
        return new ResponseEntity<>(subService.findAllSubmissionsWithQuizID(id), HttpStatus.OK);
    }


    @PreAuthorize("hasAnyRole('ADMIN' , 'TEACHER' )")
    @PutMapping("td/submission/updateGrade/{studentId}/{quizId}/{questionid}/{newGrade}")
    public ResponseEntity<?> updateAnswerGrade(@PathVariable int studentId,@PathVariable int quizId,@PathVariable int questionid,@PathVariable float newGrade){
         subService.updateAnswerGrade(studentId,quizId,questionid,newGrade);
         return ResponseEntity.ok("Submission grade updated successfully!");
    }






}
