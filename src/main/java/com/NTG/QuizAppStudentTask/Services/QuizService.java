package com.NTG.QuizAppStudentTask.Services;
import com.NTG.QuizAppStudentTask.DTO.QuestionDTO;
import com.NTG.QuizAppStudentTask.DTO.QuizDTO;
import com.NTG.QuizAppStudentTask.DTO.QuizResultDTO;
import com.NTG.QuizAppStudentTask.DTO.QuizWithquestionsDTO;
import com.NTG.QuizAppStudentTask.Models.*;
import com.NTG.QuizAppStudentTask.Repositories.QuizRepo;
import com.NTG.QuizAppStudentTask.Repositories.questionRepo;
import com.NTG.QuizAppStudentTask.Repositories.userRepo;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizService {

   private final QuizRepo quizRepo;
   private final userRepo userrepo;
   private final questionRepo questionRepo;

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
                            status.name(),
                            quiz.getCreatedBy()
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
                status.name(),
                quiz.getCreatedBy()

        );


    }

    public List<QuizResultDTO>  getAllResults() {
     return quizRepo.findStudentQuizResults();
    }

    public QuizDTO getteacherQuizById(int id, int teacherId) {
        Optional<QuizDTO> quiz = quizRepo.findByIdAndTeacherId(id, teacherId);
        if (quiz.isPresent()) {
            return quiz.get();
        } else {
            throw new RuntimeException("Quiz not found");
        }

    }
    public void createQuizWithQuestions(QuizWithquestionsDTO dto) {

        // Get current authenticated user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();

        String username = (principal instanceof UserDetails)
                ? ((UserDetails) principal).getUsername()
                : principal.toString();

        User user = userrepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Calculate end time based on start time + duration
        LocalDateTime endTime = dto.getStartTime().plusMinutes(dto.getDuration());

        // Create Quiz entity
        Quiz quiz = new Quiz();
        quiz.setTitle(dto.getTitle());
        quiz.setDescription(dto.getDescription());
        quiz.setStartTime(dto.getStartTime());
        quiz.setEndTime(endTime);
        quiz.setStatus(dto.getStatus() == null
                ? Quiz.Status.SCHEDULED
                : Quiz.Status.valueOf(dto.getStatus()));
        quiz.setCreatedByUser(user);

        quizRepo.save(quiz);

        // Create questions with options
        if (dto.getQuestions() != null) {
            for (QuestionDTO qDto : dto.getQuestions()) {
                Question question = new Question();
                question.setText(qDto.getText());
                question.setGrade(qDto.getGrade());
                question.setQuestionType(Question.QuestionType.valueOf(qDto.getType().toUpperCase()));
                question.setQuiz(quiz);

                // Map OptionDTOs to Option entities
                List<Option> options = qDto.getOptions().stream()
                        .map(optDto -> {
                            Option opt = new Option();
                            opt.setAnswer(optDto.getAnswer());  // النص الفعلي للإجابة
                            opt.setCorrect(optDto.isCorrect()); // هل هذه الإجابة صحيحة
                            opt.setQuestion(question);
                            return opt;
                        }).collect(Collectors.toList());

                question.setOptions(options);

                questionRepo.save(question);
            }
        }
    }



    //teacher update in quiz by id
    public QuizDTO updateQuiz(int quizId, QuizDTO quizDTO){

        User user = userrepo.findById(1)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Quiz quiz=quizRepo.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
        //ensure quiz is created by this teacher if quiz created by this teacher do update
        //replace user with the current user
        if(quiz.getCreatedByUser()==user){
            quiz.setTitle(quizDTO.getTitle());
            quiz.setDescription(quizDTO.getDescription());
            quiz.setStartTime( quizDTO.getStartTime());
            quiz.setEndTime(quizDTO.getEndTime());
            quiz.setStatus(Quiz.Status.valueOf(quizDTO.getStatus()));
            quiz.setCreatedByUser(user);
            quiz.setUpdatedAt(LocalDateTime.now());
            quiz.setUpdatedBy(1);
            quizRepo.save(quiz);
            return new QuizDTO(
                    quiz.getId(),
                    quiz.getTitle(),
                    quiz.getDescription(),
                    quiz.getStartTime(),
                    quiz.getEndTime(),
                    quiz.getStatus().name(),
                    quiz.getCreatedBy()

            );
        }
        else
        {
            throw new RuntimeException("NO quiz created by you with this id");
        }

    }

    //teacher delete in quiz by id
    public void deleteQuiz(int id) {
        Quiz quiz = quizRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
        // quizRepo.delete(quiz);
        if (quiz.getIsDeleted() == false) {
            quiz.setIsDeleted(true);
            quizRepo.save(quiz);
        }
        else
        {
            throw new RuntimeException("this quiz is already deleted");
        }
    }


}
