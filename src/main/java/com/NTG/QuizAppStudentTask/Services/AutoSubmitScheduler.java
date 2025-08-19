package com.NTG.QuizAppStudentTask.Services;

import com.NTG.QuizAppStudentTask.Models.Submission;
import com.NTG.QuizAppStudentTask.Repositories.SubmissionRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AutoSubmitScheduler {
    private final SubmissionRepo submissionRepo;

    @Scheduled(fixedRate = 60_000) // كل دقيقة
    @Transactional
    public void autoSubmitExpired() {
        LocalDateTime now = LocalDateTime.now();
        List<Submission> expired = submissionRepo.findExpiredUnsubmitted(now);

        for (Submission s : expired) {
            s.setAutoSubmitted(true);
            s.setSubmittedAt(s.getDeadline()); // قفلناها عند الديدلاين
            // مفيش إجابات إضافية لو انتي مش عاملة progressive save
            submissionRepo.save(s);
        }
    }
}