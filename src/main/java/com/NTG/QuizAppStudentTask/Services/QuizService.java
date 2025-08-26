package com.NTG.QuizAppStudentTask.Services;
import com.NTG.QuizAppStudentTask.DTO.QuizDTO;
import com.NTG.QuizAppStudentTask.DTO.QuizResultDTO;
import com.NTG.QuizAppStudentTask.Models.Quiz;
import com.NTG.QuizAppStudentTask.Models.Submission;
import com.NTG.QuizAppStudentTask.Models.User;
import com.NTG.QuizAppStudentTask.Repositories.QuizRepo;
import com.NTG.QuizAppStudentTask.Repositories.userRepo;
import com.NTG.QuizAppStudentTask.Config.AuditorAwareImpl;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
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
  private final AuditorAwareImpl auditorAwareImpl;

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

                    QuizDTO DTO=new QuizDTO();
                    DTO.setTitle(quiz.getTitle());
                    DTO.setDescription(quiz.getDescription());
                    DTO.setEndTime(quiz.getEndTime());
                    DTO.setStartTime(quiz.getStartTime());
                    DTO.setStatus( quiz.getStatus().name());
                    return DTO;
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

        QuizDTO DTO=new QuizDTO();
        DTO.setTitle(quiz.getTitle());
        DTO.setDescription(quiz.getDescription());
        DTO.setEndTime(quiz.getEndTime());
        DTO.setStartTime(quiz.getStartTime());
        DTO.setStatus( quiz.getStatus().name());
        return DTO;

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

    //create quiz by teacher and admin
    public QuizDTO createQuiz(QuizDTO quiz){


        Quiz quiz2=new Quiz();
        quiz2.setTitle(quiz.getTitle());
        quiz2.setDescription(quiz.getDescription());
        quiz2.setStartTime( quiz.getStartTime());
        quiz2.setEndTime(quiz.getEndTime());
        quiz2.setStatus(Quiz.Status.valueOf(quiz.getStatus()));
        Optional<String> userNameOpt = auditorAwareImpl.getCurrentAuditor();
        User user = userrepo.findByUsername(userNameOpt.get())
                .orElseThrow(() -> new RuntimeException("User not found"));

        quiz2.setCreatedByUser(user);
        quizRepo.save(quiz2);
        QuizDTO DTO=new QuizDTO();
        DTO.setTitle(quiz2.getTitle());
        DTO.setDescription(quiz2.getDescription());
        DTO.setEndTime(quiz2.getEndTime());
        DTO.setStartTime(quiz2.getStartTime());
        DTO.setStatus( quiz2.getStatus().name());
        return DTO;
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

    //create quiz by teacher and admin
    public QuizDTO createQuiz(QuizDTO quiz){

        Quiz quiz2=new Quiz();
        quiz2.setTitle(quiz.getTitle());
        quiz2.setDescription(quiz.getDescription());
        quiz2.setStartTime( quiz.getStartTime());
        quiz2.setEndTime(quiz.getEndTime());
        quiz2.setStatus(Quiz.Status.valueOf(quiz.getStatus()));


        User user = userrepo.findById(quiz.getCreatedByUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        quiz2.setCreatedByUser(user);

        quizRepo.save(quiz2);
        return new QuizDTO(
                 quiz2.getId(),
                quiz2.getTitle(),
                quiz2.getDescription(),
                quiz2.getStartTime(),
                quiz2.getEndTime(),
                quiz2.getStatus().name(),
                quiz2.getCreatedBy()
        );
    }

  
    //teacher update in quiz by id
    public QuizDTO updateQuiz(int quizId, QuizDTO quizDTO){

        Quiz quiz=quizRepo.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
        //ensure quiz is created by this teacher if quiz created by this teacher do update
        //replace user with the current user

        if(quiz.getCreatedBy()==auditorAwareImpl.getCurrentAuditor().get()){
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
