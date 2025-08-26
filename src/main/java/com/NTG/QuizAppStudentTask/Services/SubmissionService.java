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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.io.IOException;


@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final SubmissionRepo submissionRepo;
    private final SubmissionAnswerRepo submissionAnswerRepo;
    private final questionRepo questionRepo;
    private final QuizRepo quizRepo;
    private final userRepo userRepo;


    private final GradeWrittenAnswer gradingService;

    public void saveSubmission(SubmissionRequest subrequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new RuntimeException("User not authenticated");
        }

        String username = auth.getName();
        User student = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));


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
                        subanswer.setGrade(isCorrect ? question.getGrade() : 0);
                    }// written question
                    else if (question.getQuestionType() == Question.QuestionType.WRITTEN) {

                        String studentAns = reqdto.getStudentAnswer();
                        String modelAns = question.getModelAnswer();
                        try {
                            System.out.println("StudentAns: " + studentAns);
                            System.out.println("ModelAns: " + modelAns);
                            System.out.println("QuestionGrade: " + question.getGrade());
                            double scorePercent = gradingService.gradeAnswer(studentAns, modelAns);
                            System.out.println("ScorePercent: " + scorePercent);
                            float score = (float) ((scorePercent / 100.0) * question.getGrade());
                            subanswer.setGrade(score);
                            subanswer.setCorrect(scorePercent >= 70);
                        }
                        catch (IOException e) {
                            subanswer.setGrade(0);
                            subanswer.setCorrect(false);
                            e.printStackTrace();
                        }

                    }

                    return subanswer;
                }).collect(Collectors.toList());

        submission.setSubmissionAnswers(submissionAnswers);

        //  calculate total grade

        float totalGrade = (int) submissionAnswers.stream()
                .mapToDouble(SubmissionAnswer::getGrade)
                .sum();
        submission.setTotalGrade(totalGrade);
        submissionRepo.save(submission);
    }


    public List<SubmissionSummaryDTO> findAllSubmissionsWithQuizID(int quizid) {
        return submissionRepo.findSubmissionsByQuizId(quizid);
    }

    //teacher can update manually
    public void updateAnswerGrade(int studentId,int quizId,int questionId,float newGrade){
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//
//        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
//            throw new RuntimeException("User not authenticated");
//        }

       // String username = auth.getName();
        User student = userRepo.findById(studentId)
                .orElseThrow(() -> new RuntimeException("student not found"));

        Quiz quiz =quizRepo.findById(quizId).orElseThrow(() -> new RuntimeException("quiz not found"));

        Submission submission = submissionRepo.findByStudentAndQuiz(student,quiz)
                .orElseThrow(() -> new RuntimeException("Submission not found"));


        SubmissionAnswer submissionAnswer = submissionAnswerRepo
                .findBySubmissionAndQuestionId(submission, (long)questionId)
                .orElseThrow(() -> new RuntimeException("SubmissionAnswer not found for this question"));

        submissionAnswer.setGrade(newGrade);
        submissionAnswer.setCorrect(newGrade > 0);
        submissionAnswerRepo.save(submissionAnswer);

        float totalGrade = (float) submission.getSubmissionAnswers()
                .stream()
                .mapToDouble(SubmissionAnswer::getGrade)
                .sum();
        submission.setTotalGrade(totalGrade);

        submissionRepo.save(submission);
    }

}


