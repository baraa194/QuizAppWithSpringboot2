package com.NTG.QuizAppStudentTask.Services;

import com.NTG.QuizAppStudentTask.DTO.SubmissionRequest;
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

     // get the submission
        Submission submission = submissionRepo
                .findByStudentAndQuiz(student, quiz)
                .orElseThrow(() -> new RuntimeException("Submission not started for this quiz"));


        if (submission.getStatus() == Submission.Status.COMPLETED) {
            throw new RuntimeException("You have already submitted this quiz");
        }

        // ⏳ deadline check
        if (quiz.getEndTime() != null && LocalDateTime.now().isAfter(quiz.getEndTime())) {
            throw new RuntimeException("Quiz time has ended, submission not allowed");
        }

        //  set submittedAt & status to COMPLETED
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setStatus(Submission.Status.COMPLETED);

        // build submission answers
        List<SubmissionAnswer> submissionAnswers = subrequest.getAnswers().stream().map(
                reqdto -> {
                    SubmissionAnswer subanswer = new SubmissionAnswer();
                    subanswer.setSubmission(submission);
                    subanswer.setStudentAnswer(reqdto.getStudentaswer());

                    Question question = questionRepo.findById(reqdto.getQuestionid())
                            .orElseThrow(() -> new RuntimeException("Question not found"));
                    subanswer.setQuestion(question);

                    //  Auto grading
                    if (question.getCorrectOption() != null) {
                        boolean isCorrect = question.getCorrectOption()
                                .equalsIgnoreCase(reqdto.getStudentaswer());
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

        //  calculate total grade
        float totalGrade = (float) submissionAnswers.stream()
                .mapToDouble(SubmissionAnswer::getManualGrade)
                .sum();
        submission.setTotalGrade(totalGrade);

        submissionRepo.save(submission);
    }
}


