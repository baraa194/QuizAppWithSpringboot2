package com.NTG.QuizAppStudentTask.Services;

import com.NTG.QuizAppStudentTask.DTO.SubmissionRequest;
import com.NTG.QuizAppStudentTask.DTO.SubmissionSummaryDTO;
import com.NTG.QuizAppStudentTask.Models.*;
import com.NTG.QuizAppStudentTask.Repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.io.IOException;


@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final SubmissionRepo submissionRepo;
    private final SubmissionAnswerRepo submissionAnswerRepo;
    private final questionRepo questionRepo;
    private final QuizRepo quizRepo;
    private final userRepo userRepo;


    private final GradeWrittenAnswer gradingService;

    @Transactional
    public void saveSubmission(SubmissionRequest subrequest) {

        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new RuntimeException("User not authenticated");
        }
        var username = auth.getName();
        var student = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        int submissionId = subrequest.getSubmissionId();
        Submission submission = submissionRepo.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        if (submission.getStudent() == null || submission.getStudent().getId() != student.getId()) {
            throw new RuntimeException("Forbidden: submission does not belong to current user");
        }

        if (submission.getStatus() == Submission.Status.COMPLETED) {
            throw new RuntimeException("You have already submitted this quiz");
        }
        if (submission.getDeadline() != null && LocalDateTime.now().isAfter(submission.getDeadline())) {
            throw new RuntimeException("Quiz time has ended, submission not allowed");
        }

        List<SubmissionAnswer> answersToSave = new ArrayList<>();

        for (var reqdto : subrequest.getAnswers()) {
            int qId = reqdto.getQuestionId();
            Question question = questionRepo.findById(qId)
                    .orElseThrow(() -> new RuntimeException("Question not found"));

            SubmissionAnswer sa = submissionAnswerRepo
                    .findBySubmissionIdAndQuestionId(submission.getId(), question.getId())
                    .orElse(new SubmissionAnswer());

            sa.setSubmission(submission);
            sa.setQuestion(question);

            boolean isMcq     = question.getQuestionType() == Question.QuestionType.MCQ;
            boolean isWritten = question.getQuestionType() == Question.QuestionType.WRITTEN;

            if (isMcq) {
                int selId = reqdto.getSelectedOptionId();

                if (selId <= 0) {
                    // لا توجد إجابة
                    sa.setSelectedOptionId(0);
                    sa.setStudentAnswer("");
                    sa.setCorrect(false);
                    sa.setManualGrade(0f);
                } else {
                    Option matchedOpt = question.getOptions().stream()
                            .filter(o -> o.getId() == selId)
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Selected option does not belong to this question"));

                    // سجّل الاختيار
                    sa.setSelectedOptionId(selId);
                    sa.setStudentAnswer("");

                    boolean correct = matchedOpt.isCorrect();
                    sa.setCorrect(correct);

                    float qGrade = question.getGrade();
                    if (qGrade <= 0f) qGrade = 1f;
                    sa.setManualGrade(correct ? qGrade : 0f);
                }

            } else if (isWritten) {

                sa.setSelectedOptionId(0);
                String studentAns = reqdto.getStudentAnswer() == null ? "" : reqdto.getStudentAnswer().trim();
                sa.setStudentAnswer(studentAns);

                // اجلب الـ modelAnswer من السؤال
                String model = question.getModelAnswer() == null ? "" : question.getModelAnswer().trim();

                float qGrade = question.getGrade();
                if (qGrade <= 0f) qGrade = 1f;

                float manual = 0f;
                boolean correct = false;

                try {
                    // درجة التشابه (0..100) من خدمة GradeWrittenAnswer
                    double sim = gradingService.gradeAnswer(studentAns, model);

                    // سياسات الدرجة: ≥90% كاملة، ≥80% نصف درجة، غير ذلك صفر
                    if (sim >= 90.0) {
                        manual = qGrade;
                        correct = true;
                    } else if (sim >= 80.0) {
                        manual = qGrade * 0.5f;
                        correct = false;
                    } else {
                        manual = 0f;
                        correct = false;
                    }
                } catch (Exception ex) {
                    // في حال أي خطأ أثناء التصحيح الآلي، لا نفجّر السيستم
                    manual = 0f;
                    correct = false;
                    // log.warn("Auto-grade (written) failed", ex);
                }

                sa.setCorrect(correct);
                sa.setManualGrade(manual);

            } else {
                // أنواع أخرى (لو موجودة لاحقاً)
                sa.setSelectedOptionId(0);
                String ans = reqdto.getStudentAnswer() == null ? "" : reqdto.getStudentAnswer();
                sa.setStudentAnswer(ans);
                sa.setCorrect(false);
                sa.setManualGrade(0f);
            }

            answersToSave.add(sa);
        }

        submissionAnswerRepo.saveAll(answersToSave);
        submission.setSubmissionAnswers(answersToSave);

        float total = 0f;
        for (SubmissionAnswer a : answersToSave) {
            total += a.getManualGrade();
        }
        submission.setTotalGrade(total);

        LocalDateTime now = LocalDateTime.now();
        submission.setSubmittedAt(now);
        submission.setStatus(Submission.Status.COMPLETED);

        // لو محتاجة حساب زمن المحاولة ارجعي فعّلي الكود التالي
        // if (submission.getStartedAt() != null) {
        //     long seconds = ChronoUnit.SECONDS.between(submission.getStartedAt(), now);
        //     submission.setTimeSpentSeconds((int) Math.max(0, seconds));
        // }

        submissionRepo.save(submission);
    }





    public List<SubmissionSummaryDTO> findAllSubmissionsWithQuizID(int quizid) {
        return submissionRepo.findSubmissionsByQuizId(quizid);
    }

    //teacher can update manually
    public void updateAnswerGrade(int studentId,int quizId,int questionId,float newGrade){
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//
//        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
//            throw new RuntimeException("User not authenticated");
//        }

       // String username = auth.getName();
        User student = userRepo.findById(studentId)
                .orElseThrow(() -> new RuntimeException("student not found"));

        Quiz quiz =quizRepo.findById(quizId).orElseThrow(() -> new RuntimeException("quiz not found"));

        Submission submission = submissionRepo.findByStudentAndQuiz(student,quiz)
                .orElseThrow(() -> new RuntimeException("Submission not found"));


        SubmissionAnswer submissionAnswer = submissionAnswerRepo
                .findBySubmissionAndQuestionId(submission, (long)questionId)
                .orElseThrow(() -> new RuntimeException("SubmissionAnswer not found for this question"));

        submissionAnswer.setManualGrade(newGrade);
        submissionAnswer.setCorrect(newGrade > 0);
        submissionAnswerRepo.save(submissionAnswer);

        float totalGrade = (float) submission.getSubmissionAnswers()
                .stream()
                .mapToDouble(SubmissionAnswer::getManualGrade)
                .sum();
        submission.setTotalGrade(totalGrade);

        submissionRepo.save(submission);
    }

}


