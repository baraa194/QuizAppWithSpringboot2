package com.NTG.QuizAppStudentTask.Services;

import com.NTG.QuizAppStudentTask.DTO.QuestionDTO;
import com.NTG.QuizAppStudentTask.Models.Question;
import com.NTG.QuizAppStudentTask.Models.Quiz;
import com.NTG.QuizAppStudentTask.Repositories.QuizRepo;
import com.NTG.QuizAppStudentTask.Repositories.questionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final questionRepo questionRepo;
    private final QuizRepo quizRepo;

    public List<QuestionDTO> findQuesByquiz(Integer quizId)
    {

      List<Question>  questions = questionRepo.findByQuizId(quizId);
        return questions.stream()
                .map(q -> new QuestionDTO(

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

    public QuestionDTO addQuestion(int quizId, QuestionDTO questionDTO) {
        Quiz quiz = quizRepo.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        Question question = new Question();
        question.setText(questionDTO.getText());
        question.setQuestionType(Question.QuestionType.valueOf(questionDTO.getType()));
        question.setOptionA(questionDTO.getOptionA());
        question.setOptionB(questionDTO.getOptionB());
        question.setOptionC(questionDTO.getOptionC());
        question.setOptionD(questionDTO.getOptionD());
        question.setCorrectOption(questionDTO.getCorrectOption());
        question.setGrade(questionDTO.getGrade());
        question.setQuiz(quiz);

        Question saved = questionRepo.save(question);


        QuestionDTO response = new QuestionDTO();

        response.setText(saved.getText());
        response.setType(saved.getQuestionType().name());
        response.setOptionA(saved.getOptionA());
        response.setOptionB(saved.getOptionB());
        response.setOptionC(saved.getOptionC());
        response.setOptionD(saved.getOptionD());
        response.setCorrectOption(saved.getCorrectOption());
        response.setGrade(saved.getGrade());

        return response;
    }

    public QuestionDTO updateQuestionInQuiz(int quizId, int questionId, QuestionDTO dto) {
        Quiz quiz = quizRepo.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        Question question = quiz.getQuestions().stream()
                .filter(q -> q.getId() == questionId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Question not found in this quiz"));

        question.setText(dto.getText());
        question.setQuestionType(Question.QuestionType.valueOf(dto.getType()));
        question.setOptionA(dto.getOptionA());
        question.setOptionB(dto.getOptionB());
        question.setOptionC(dto.getOptionC());
        question.setOptionD(dto.getOptionD());
        question.setCorrectOption(dto.getCorrectOption());
        question.setGrade(dto.getGrade());
        question.setQuiz(quiz);

        Question updated = questionRepo.save(question);


        QuestionDTO response = new QuestionDTO();

        response.setText(updated.getText());
        response.setType(updated.getQuestionType().name());
        response.setOptionA(updated.getOptionA());
        response.setOptionB(updated.getOptionB());
        response.setOptionC(updated.getOptionC());
        response.setOptionD(updated.getOptionD());
        response.setCorrectOption(updated.getCorrectOption());
        response.setGrade(updated.getGrade());

        return response;
    }



    public void deleteQuestion(int quizId,int questionId){
        Quiz quiz=quizRepo.findById(quizId).orElseThrow(() -> new RuntimeException("Quiz not found"));
        Question question= quiz.getQuestions().stream().filter(q -> q.getId() == questionId).findFirst()
                .orElseThrow(() -> new RuntimeException("Question not found in this quiz"));

        if(!question.getIsDeleted()){
            question.setIsDeleted(true);
            questionRepo.save(question);
        }
        else
            throw new RuntimeException("this is deleted already");
    }















}
