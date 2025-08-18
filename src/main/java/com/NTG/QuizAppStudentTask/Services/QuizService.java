package com.NTG.QuizAppStudentTask.Services;

import com.NTG.QuizAppStudentTask.DTO.QuizDTO;
import com.NTG.QuizAppStudentTask.Models.Quiz;
import com.NTG.QuizAppStudentTask.Models.User;
import com.NTG.QuizAppStudentTask.Repositories.QuizRepo;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.NTG.QuizAppStudentTask.Repositories.userRepo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.NTG.QuizAppStudentTask.Mapper.QuizMapper;
import java.util.Collections;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizService {

   private final QuizRepo quizRepo;
   private final userRepo UserRepo;


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
                           // quiz.getId(),
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
                //quiz.getId(),
                quiz.getTitle(),
                quiz.getDescription(),
                quiz.getStartTime(),
                quiz.getEndTime(),
                status.name()

        );


    }

    public void AddQuizSubmission(QuizDTO quizDTO) {

    }

    //getquiz by teacher (i think need to integrate correctly )
    public QuizDTO getQuizById(int id){
        Quiz quiz = quizRepo.findById(id).orElse(null);
       return  QuizMapper.toDTO(quiz);
    }

    //create quiz by teacher and admin
    public QuizDTO createQuiz(QuizDTO quiz){

        Quiz quiz2=new Quiz();
        quiz2.setTitle(quiz.getTitle());
        quiz2.setDescription(quiz.getDescription());
        quiz2.setStartTime( quiz.getStartTime());
        quiz2.setEndTime(quiz.getEndTime());
        quiz2.setStatus(Quiz.Status.valueOf(quiz.getStatus()));


        User user = UserRepo.findById(quiz.getCreatedByUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        quiz2.setCreatedByUser(user);

        quizRepo.save(quiz2);
        return new QuizDTO(
                //quiz.getId(),
                quiz2.getTitle(),
                quiz2.getDescription(),
                quiz2.getStartTime(),
                quiz2.getEndTime(),
                quiz2.getStatus().name()
        );
    }

    //get quizzes by teacher id
   public List<QuizDTO> getTeacherQuizzes(int id){
       User user = UserRepo.findById(id)
               .orElseThrow(() -> new RuntimeException("User not found"));
       List<Quiz> quizzes = user.getQuizzes() != null ? user.getQuizzes() : Collections.emptyList();
       List<QuizDTO> DtoList= quizzes.stream().map(quiz -> QuizMapper.toDTO(quiz))
               .collect(Collectors.toList());

       return DtoList;
   }

   //teacher update in quiz by id
    public QuizDTO updateQuiz(int quizId, QuizDTO quizDTO){

        User user = UserRepo.findById(1)
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
            return QuizMapper.toDTO(quiz);
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
    //teacher publish quiz by id
    public void publishQuiz(int id){
        Quiz quiz = quizRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
        if (quiz.getIsDeleted() == false) {
            quiz.setIsActive(true);
            quizRepo.save(quiz);
        }
        else
            throw new RuntimeException("this quiz is deleted");
    }






}
