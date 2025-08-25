package com.NTG.QuizAppStudentTask.Services;

import com.NTG.QuizAppStudentTask.DTO.StartQuizRequestDTO;
import com.NTG.QuizAppStudentTask.DTO.startQuizResponseDTO;
import com.NTG.QuizAppStudentTask.DTO.QuestionStudentDTO;
import com.NTG.QuizAppStudentTask.Models.Quiz;
import com.NTG.QuizAppStudentTask.Models.Submission;
import com.NTG.QuizAppStudentTask.Models.User;
import com.NTG.QuizAppStudentTask.Repositories.QuizRepo;
import com.NTG.QuizAppStudentTask.Repositories.SubmissionRepo;
import com.NTG.QuizAppStudentTask.Repositories.userRepo;
import com.NTG.QuizAppStudentTask.Services.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor

public class QuizAttemptService {
    private final QuizRepo quizrepo;
    private final userRepo userrepo;
    private final SubmissionRepo submissionRepo;
    private final QuestionService questionService;

    public startQuizResponseDTO startQuiz(StartQuizRequestDTO req) {
        Quiz quiz = quizrepo.findById(req.getQuizId())
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

//        if (quiz.getStatus() != Quiz.Status.IN_PROGRESS)
//            throw new RuntimeException("Quiz is not active");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new RuntimeException("User not authenticated");
        }

        String username = auth.getName();
        User student = userrepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("student not found"));

        Optional<Submission> existingSubmission = submissionRepo.findByStudentAndQuiz(student, quiz);

        if (existingSubmission.isPresent()) {
            Submission submission = existingSubmission.get();
            if (submission.getStatus() == Submission.Status.COMPLETED) {
                throw new RuntimeException("You have already completed this quiz");
            }

            List<QuestionStudentDTO> questions = questionService.findQuesForStudent(quiz.getId());

            return new startQuizResponseDTO(
                    submission.getId(),
                    submission.getDeadline(),
                    quiz.getTitle(),
                    questions
            );
        }

        //  check deadline
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadline = quiz.getEndTime();
        if (deadline == null) throw new RuntimeException("Quiz endTime is not set");
        if (now.isAfter(deadline)) throw new RuntimeException("Quiz time is over");

        //  create new submission
        Submission sub = new Submission();
        sub.setStudent(student);
        sub.setQuiz(quiz);
        sub.setStartedAt(now);
        sub.setDeadline(deadline);
        sub.setStatus(Submission.Status.IN_PROGRESS);
        sub.setSubmittedAt(LocalDateTime.now());
        sub = submissionRepo.save(sub);


        List<QuestionStudentDTO> questions = questionService.findQuesForStudent(quiz.getId());

        return new startQuizResponseDTO(
                sub.getId(),
                sub.getDeadline(),
                quiz.getTitle(),
                questions
        );
    }



}
