package com.NTG.QuizAppStudentTask.Services;

import com.NTG.QuizAppStudentTask.DTO.StartQuizRequestDTO;
import com.NTG.QuizAppStudentTask.DTO.startQuizResponseDTO;
import com.NTG.QuizAppStudentTask.Models.Quiz;
import com.NTG.QuizAppStudentTask.Models.Submission;
import com.NTG.QuizAppStudentTask.Models.User;
import com.NTG.QuizAppStudentTask.Repositories.QuizRepo;
import com.NTG.QuizAppStudentTask.Repositories.SubmissionRepo;
import com.NTG.QuizAppStudentTask.Repositories.userRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor

public class QuizAttemptService {
    private final QuizRepo quizrepo;
    private final userRepo userrepo;
    private final SubmissionRepo submissionRepo;

    public startQuizResponseDTO startQuiz(StartQuizRequestDTO req) {
        Quiz quiz = quizrepo.findById(req.getQuizId())
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        if (quiz.getStatus() != Quiz.Status.IN_PROGRESS)
            throw new RuntimeException("Quiz is not active");

        User student = userrepo.findById(req.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));


        Optional<Submission> existingSubmission = submissionRepo.findByStudentAndQuiz(student, quiz);

        if (existingSubmission.isPresent()) {
            Submission submission = existingSubmission.get();
            if (submission.getStatus() == Submission.Status.COMPLETED) {
                throw new RuntimeException("You have already completed this quiz");
            }

            return new startQuizResponseDTO(
                    submission.getId(),
                    submission.getDeadline(),
                    quiz.getTitle()
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

        sub = submissionRepo.save(sub);

        return new startQuizResponseDTO(sub.getId(), sub.getDeadline(), quiz.getTitle());
    }



}
