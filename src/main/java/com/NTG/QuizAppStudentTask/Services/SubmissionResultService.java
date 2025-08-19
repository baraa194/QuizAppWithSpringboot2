package com.NTG.QuizAppStudentTask.Services;

import com.NTG.QuizAppStudentTask.DTO.SubmissionResponse;
import com.NTG.QuizAppStudentTask.DTO.submissionAnswerResponse;
import com.NTG.QuizAppStudentTask.Models.Submission;
import com.NTG.QuizAppStudentTask.Repositories.SubmissionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubmissionResultService {

    private final SubmissionRepo submissionRepo;

    public SubmissionResponse getSubmissionResult(int submissionId) {
        Submission sub = submissionRepo.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        SubmissionResponse resultDTO = new SubmissionResponse();
        resultDTO.setStudentId(sub.getStudent().getId());
        resultDTO.setSubmissionId(sub.getId());
        resultDTO.setQuizId(sub.getQuiz().getId());
        resultDTO.setQuizTitle(sub.getQuiz().getTitle());
        resultDTO.setTotalGrade(sub.getTotalGrade());

        List<submissionAnswerResponse> answers = sub.getSubmissionAnswers().stream().map(a -> {
            submissionAnswerResponse ansDTO = new submissionAnswerResponse();
            ansDTO.setQuestionId(a.getQuestion().getId());
            ansDTO.setQuestionText(a.getQuestion().getText());
            ansDTO.setStudentAnswer(a.getStudentAnswer());
            ansDTO.setCorrect(a.isCorrect());
            ansDTO.setGrade(a.getManualGrade());
            return ansDTO;
        }).toList();

        resultDTO.setAnswers(answers);
        return resultDTO;
    }





}
