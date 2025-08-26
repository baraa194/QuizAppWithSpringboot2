package com.NTG.QuizAppStudentTask.Services;

import com.NTG.QuizAppStudentTask.DTO.OptionDTO;
import com.NTG.QuizAppStudentTask.DTO.QuestionDTO;
import com.NTG.QuizAppStudentTask.Models.Option;
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
                        q.getGrade()
                ))
                .collect(Collectors.toList());
    }

    /** Add a question to a quiz */
    public QuestionDTO addQuestion(int quizId, QuestionDTO questionDTO) {
        Quiz quiz = quizRepo.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        Question question = new Question();
        question.setText(questionDTO.getText());
        question.setQuestionType(Question.QuestionType.valueOf(questionDTO.getType()));
        question.setGrade(questionDTO.getGrade());
        question.setQuiz(quiz);

        // Map OptionDTOs to Options (بدون id)
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

        // Build DTO to return (بدون id)
        return new QuestionDTO(
                saved.getText(),
                saved.getOptions().stream()
                        .map(opt -> new OptionDTO(opt.isCorrect(), opt.getAnswer()))
                        .collect(Collectors.toList()),
                saved.getQuestionType().name(),
                saved.getGrade()
        );
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

        // Update options (رجع answer مع isCorrect)
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
                updated.getGrade()
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




