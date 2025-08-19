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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/quiz/submission")
public class SubmissionController {

    @Autowired
    private SubmissionService subService;
    @Autowired
    private  QuizAttemptService attemptService;
    @Autowired
    private SubmissionResultService resultService;

    @PostMapping("/start")
    public startQuizResponseDTO start(@RequestBody StartQuizRequestDTO req)
    { return attemptService.startQuiz(req); }

    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody SubmissionRequest submissionReq) {

        subService.saveSubmission(submissionReq);
        return ResponseEntity.ok("Submission saved and graded successfully!");

    }


    @GetMapping("/submissionresult/{id}")
    public ResponseEntity<?>  getsubmissions(@PathVariable int id)
    {
        return new ResponseEntity<>(resultService.getSubmissionResult(id), HttpStatus.OK);

    }





}
