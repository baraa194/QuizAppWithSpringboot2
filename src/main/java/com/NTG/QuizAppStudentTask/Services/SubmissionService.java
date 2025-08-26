package com.NTG.QuizAppStudentTask.Services;

import com.NTG.QuizAppStudentTask.DTO.SubmissionRequest;
import com.NTG.QuizAppStudentTask.DTO.SubmissionSummaryDTO;
import com.NTG.QuizAppStudentTask.Models.*;
import com.NTG.QuizAppStudentTask.Repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final SubmissionRepo submissionRepo;
    private final SubmissionAnswerRepo submissionAnswerRepo;
    private final questionRepo questionRepo;
    private final QuizRepo quizRepo;
    private final userRepo userRepo;

    public void saveSubmission(SubmissionRequest subrequest) {
        User student = userRepo.findById(subrequest.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Quiz quiz = quizRepo.findById(subrequest.getQuizId())
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        Submission submission = submissionRepo
                .findByStudentAndQuiz(student, quiz)
                .orElseThrow(() -> new RuntimeException("Submission not started for this quiz"));

        if (submission.getStatus() == Submission.Status.COMPLETED) {
            throw new RuntimeException("You have already submitted this quiz");
        }

        if (quiz.getEndTime() != null && LocalDateTime.now().isAfter(quiz.getEndTime())) {
            throw new RuntimeException("Quiz time has ended, submission not allowed");
        }

        submission.setSubmittedAt(LocalDateTime.now());
        submission.setStatus(Submission.Status.COMPLETED);

        List<SubmissionAnswer> submissionAnswers = subrequest.getAnswers().stream().map(
                reqdto -> {
                    SubmissionAnswer subanswer = new SubmissionAnswer();
                    subanswer.setSubmission(submission);
                    subanswer.setStudentAnswer(reqdto.getStudentAnswer());

                    Question question = questionRepo.findById(reqdto.getQuestionId())
                            .orElseThrow(() -> new RuntimeException("Question not found"));
                    subanswer.setQuestion(question);

                    // Auto grading based on options
                    if (question.getOptions() != null && !question.getOptions().isEmpty()) {
                        boolean isCorrect = question.getOptions().stream()
                                .anyMatch(opt -> opt.isCorrect() && opt.getId() == reqdto.getSelectedOptionId());
                        subanswer.setCorrect(isCorrect);
                        subanswer.setManualGrade(isCorrect ? question.getGrade() : 0);
                    } else {
                        // essay/manual question
                        subanswer.setCorrect(false);
                        subanswer.setManualGrade(0);
                    }

                    return subanswer;
                }).collect(Collectors.toList());

        submission.setSubmissionAnswers(submissionAnswers);

        float totalGrade = (float) submissionAnswers.stream()
                .mapToDouble(SubmissionAnswer::getManualGrade)
                .sum();
        submission.setTotalGrade(totalGrade);

        submissionRepo.save(submission);
    }


    public List<SubmissionSummaryDTO> findAllSubmissionsWithQuizID(int quizid) {
        return submissionRepo.findSubmissionsByQuizId(quizid);
    }
}


