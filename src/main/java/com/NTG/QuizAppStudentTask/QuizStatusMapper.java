package com.NTG.QuizAppStudentTask;

import com.NTG.QuizAppStudentTask.Models.Quiz;

public class QuizStatusMapper {
    private QuizStatusMapper() {}


    public static String toUiStatus(Quiz.Status dbStatus) {
        if (dbStatus == null) return "UNPUBLISHED";
        return switch (dbStatus) {
            case SCHEDULED -> "UNPUBLISHED";
            case IN_PROGRESS -> "ACTIVE";
            case FINISHED -> "COMPLETED";
        };
    }

    // UI -> DB (لما تحتاجيه في create/update)
    public static Quiz.Status toDbStatus(String uiStatus) {
        if (uiStatus == null || uiStatus.isBlank()) return Quiz.Status.SCHEDULED;
        return switch (uiStatus.toUpperCase()) {
            case "UNPUBLISHED" -> Quiz.Status.SCHEDULED;
            case "ACTIVE"      -> Quiz.Status.IN_PROGRESS;
            case "COMPLETED"   -> Quiz.Status.FINISHED;
            default -> Quiz.Status.SCHEDULED;
        };
    }
}
