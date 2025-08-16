package com.NTG.QuizAppStudentTask.Services;

import com.NTG.QuizAppStudentTask.DTO.QuestionDTO;
import com.NTG.QuizAppStudentTask.Models.Question;
import com.NTG.QuizAppStudentTask.Repositories.questionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private questionRepo questionRepo;

    public List<QuestionDTO> findQuesByquiz(Integer quizId)
    {

      List<Question>  questions = questionRepo.findByQuizId(quizId);
        return questions.stream()
                .map(q -> new QuestionDTO(
                        q.getId(),
                        q.getText(),
                        q.getOptionA(),
                        q.getOptionB(),
                        q.getOptionC(),
                        q.getOptionD(),
                        q.getCorrectOption(),
                        q.getQuestionType().name(),
                        q.getGrade()
                ))
                .collect(Collectors.toList());


    }




}
