package com.NTG.QuizAppStudentTask.Services;

import com.NTG.QuizAppStudentTask.DTO.*;
import com.NTG.QuizAppStudentTask.DTO.startQuizResponseDTO;
import com.NTG.QuizAppStudentTask.Models.Question;
import com.NTG.QuizAppStudentTask.Models.Quiz;
import com.NTG.QuizAppStudentTask.Models.Submission;
import com.NTG.QuizAppStudentTask.Models.User;
import com.NTG.QuizAppStudentTask.Repositories.QuizRepo;
import com.NTG.QuizAppStudentTask.Repositories.SubmissionRepo;
import com.NTG.QuizAppStudentTask.Repositories.userRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizAttemptService {

    private final QuizRepo quizRepo;
    private final userRepo userRepo;
    private final SubmissionRepo submissionRepo;

    @Transactional
    public startQuizResponseDTO startQuiz(StartQuizRequestDTO req) {

        Quiz quiz = quizRepo.findById(req.getQuizId())
                .orElseThrow(() -> new RuntimeException("Quiz not found"));


        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new RuntimeException("User not authenticated");
        }
        String username = auth.getName();
        User student = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Student not found"));


        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = quiz.getStartTime();
        LocalDateTime deadline = quiz.getEndTime();
        if (!quiz.isPublished()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Quiz is not published yet");
        }


        if (start != null && now.isBefore(start)) {
            throw new RuntimeException("Quiz has not started yet");
        }
        if (deadline == null) {
            throw new RuntimeException("Quiz endTime is not set");
        }
        if (now.isAfter(deadline)) {
            throw new RuntimeException("Quiz time is over");
        }


        Optional<Submission> existingOpt = submissionRepo.findByStudentAndQuiz(student, quiz);
        if (existingOpt.isPresent()) {
            Submission existing = existingOpt.get();
            if (existing.getStatus() == Submission.Status.COMPLETED) {
                throw new RuntimeException("You have already completed this quiz");
            }
            if (now.isAfter(existing.getDeadline())) {
                throw new RuntimeException("Quiz time is over");
            }
            List<QuestionStudentDTO> questions = buildQuestionsForStudent(quiz);
            return buildResponse(existing, quiz, questions);
        }


        Submission sub = new Submission();
        sub.setStudent(student);
        sub.setQuiz(quiz);
        sub.setStartedAt(now);
        sub.setDeadline(deadline);
        sub.setStatus(Submission.Status.IN_PROGRESS);
        sub = submissionRepo.save(sub);

        List<QuestionStudentDTO> questions = buildQuestionsForStudent(quiz);
        return buildResponse(sub, quiz, questions);
    }


    private List<QuestionStudentDTO> buildQuestionsForStudent(Quiz quiz) {
        if (quiz.getQuestions() == null) return Collections.emptyList();
        return quiz.getQuestions().stream()
                .map(this::toStudentDTO)
                .collect(Collectors.toList());
    }

    private QuestionStudentDTO toStudentDTO(Question q) {

        String type = q.getQuestionType() == null
                ? "MCQ"
                : q.getQuestionType().name();

        List<StudentOptionDTO> opts =
                (q.getQuestionType() == Question.QuestionType.MCQ && q.getOptions() != null)
                        ? q.getOptions().stream()
                        .map(o -> new StudentOptionDTO(o.getId(), o.getAnswer()))
                        .collect(Collectors.toList())
                        : Collections.emptyList();

        return new QuestionStudentDTO(
                q.getId(),
                q.getText(),
                type,
                opts
        );
    }

    private startQuizResponseDTO buildResponse(Submission sub, Quiz quiz, List<QuestionStudentDTO> questions) {
        return new startQuizResponseDTO(
                sub.getId(),
                sub.getDeadline(),
                quiz.getTitle(),
                questions
        );
    }
}

