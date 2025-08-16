package com.NTG.QuizAppStudentTask.Services;

import com.NTG.QuizAppStudentTask.DTO.QuizDTO;
import com.NTG.QuizAppStudentTask.Models.Quiz;
import com.NTG.QuizAppStudentTask.Repositories.QuizRepo;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizService {

   private final QuizRepo quizRepo;

    public List<QuizDTO> getAllWithStatus() {

        List<Quiz> quizzes = quizRepo.findAll();
        LocalDateTime now = LocalDateTime.now();

        return quizzes.stream()
                .map(quiz -> {
                    Quiz.Status status;

                    if (quiz.getStartTime().isAfter(now)) {
                        status = Quiz.Status.SCHEDULED;
                    } else if (quiz.getStartTime().isBefore(now) && quiz.getEndTime().isAfter(now)) {
                        status = Quiz.Status.IN_PROGRESS;
                    } else {
                        status = Quiz.Status.FINISHED;
                    }

                    return new QuizDTO(
                            quiz.getId(),
                            quiz.getTitle(),
                            quiz.getDescription(),
                            quiz.getStartTime(),
                            quiz.getEndTime(),
                            status.name()
                    );
                })
                .collect(Collectors.toList());
    }

    public QuizDTO getById(int id) {
        Quiz quiz = quizRepo.findById(id).orElse(null);
        LocalDateTime now = LocalDateTime.now();
        Quiz.Status status;
        if (quiz.getStartTime().isAfter(now)) {
            status = Quiz.Status.SCHEDULED;
        }
       else if (quiz.getStartTime().isBefore(now) && quiz.getEndTime().isAfter(now)) {
           status = Quiz.Status.IN_PROGRESS;
        }
       else  {
            status = Quiz.Status.FINISHED;
       }
        return new QuizDTO(
                quiz.getId(),
                quiz.getTitle(),
                quiz.getDescription(),
                quiz.getStartTime(),
                quiz.getEndTime(),
                status.name()

        );


    }

    public void AddQuizSubmission(QuizDTO quizDTO) {

    }

}
