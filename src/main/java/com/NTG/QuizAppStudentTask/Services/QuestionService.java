package com.NTG.QuizAppStudentTask.Services;

import com.NTG.QuizAppStudentTask.DTO.OptionDTO;
import com.NTG.QuizAppStudentTask.DTO.QuestionDTO;
import com.NTG.QuizAppStudentTask.Models.Option;
import com.NTG.QuizAppStudentTask.DTO.QuestionStudentDTO;
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

    /** Get all questions for a quiz */
    public List<QuestionDTO> findQuesByquiz(Integer quizId) {
        List<Question> questions = questionRepo.findByQuizId(quizId);
        return questions.stream()
                .map(q -> new QuestionDTO(
                        q.getText(),
                        q.getOptions().stream()
                                .map(opt -> new OptionDTO(opt.isCorrect(), opt.getAnswer())) // رجع answer مع isCorrect
                                .collect(Collectors.toList()),
                        q.getQuestionType().name(),
                        q.getGrade(),
                        q.getModelAnswer()

                ))
                .collect(Collectors.toList());
    }

   /* public List<QuestionStudentDTO> findQuesForStudent(Integer quizId) {
        List<Question> questions = questionRepo.findByQuizId(quizId);
        return questions.stream()
                .map(q -> new QuestionStudentDTO(
                        q.getText(),
                        q.getOptions(),
                        q.getQuestionType().name(),
                        q.getGrade()
                ))
                .collect(Collectors.toList());

    }*/


   /* public QuestionDTO addQuestion(int quizId, QuestionDTO questionDTO) {
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
        question.setModelAnswer(questionDTO.getModelAnswer());
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
        response.setModelAnswer(saved.getModelAnswer());

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

    }*/

    /** Add a question to a quiz */
    public QuestionDTO addQuestion(int quizId, QuestionDTO questionDTO) {
        Quiz quiz = quizRepo.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        Question question = new Question();
        question.setText(questionDTO.getText());
        question.setQuestionType(Question.QuestionType.valueOf(questionDTO.getType()));
        question.setGrade(questionDTO.getGrade());
        question.setQuiz(quiz);


        List<Option> options = questionDTO.getOptions().stream()
                .map(optDto -> {
                    Option opt = new Option();
                    opt.setCorrect(optDto.isCorrect());
                    opt.setAnswer(optDto.getAnswer());
                    opt.setQuestion(question);
                    return opt;
                }).collect(Collectors.toList());

        question.setOptions(options);

        Question saved = questionRepo.save(question);


        QuestionDTO resultQuestionDTO=new QuestionDTO();
        resultQuestionDTO.setText(saved.getText());
        resultQuestionDTO.setGrade(saved.getGrade());
        resultQuestionDTO.setModelAnswer(saved.getModelAnswer());
        resultQuestionDTO.setType(saved.getQuestionType().name());
        resultQuestionDTO.setOptions(saved.getOptions().stream()
                .map(opt -> new OptionDTO(opt.isCorrect(), opt.getAnswer()))
                .collect(Collectors.toList()));
        return resultQuestionDTO;
    }

    /** Update a question in a quiz */
    public QuestionDTO updateQuestionInQuiz(int quizId, int questionId, QuestionDTO dto) {
        Quiz quiz = quizRepo.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        Question question = quiz.getQuestions().stream()
                .filter(q -> q.getId() == questionId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Question not found in this quiz"));

        question.setText(dto.getText());
        question.setQuestionType(Question.QuestionType.valueOf(dto.getType()));
        question.setGrade(dto.getGrade());


        List<Option> updatedOptions = dto.getOptions().stream()
                .map(optDto -> {
                    Option opt = new Option();
                    opt.setCorrect(optDto.isCorrect());
                    opt.setAnswer(optDto.getAnswer());
                    opt.setQuestion(question);
                    return opt;
                }).collect(Collectors.toList());

        question.setOptions(updatedOptions);

        Question updated = questionRepo.save(question);

        return new QuestionDTO(
                updated.getText(),
                updated.getOptions().stream()
                        .map(opt -> new OptionDTO(opt.isCorrect(), opt.getAnswer()))
                        .collect(Collectors.toList()),
                updated.getQuestionType().name(),
                updated.getGrade(),
                updated.getModelAnswer()
        );
    }

    /** Soft delete a question */
    public void deleteQuestion(int quizId, int questionId) {
        Quiz quiz = quizRepo.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        Question question = quiz.getQuestions().stream()
                .filter(q -> q.getId() == questionId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Question not found in this quiz"));

        if (!question.getIsDeleted()) {
            question.setIsDeleted(true);
            questionRepo.save(question);
        } else {
            throw new RuntimeException("This question is already deleted");
        }
    }
}



