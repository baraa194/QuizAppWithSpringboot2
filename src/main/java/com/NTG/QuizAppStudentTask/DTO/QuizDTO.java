package com.NTG.QuizAppStudentTask.DTO;

import com.NTG.QuizAppStudentTask.Models.Quiz;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.*;

@NoArgsConstructor
//@AllArgsConstructor

@Getter @Setter
public class QuizDTO {

    private int id;
    private String title;
    private String description;

    // رجّعي UTC ISO 8601
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "UTC")
    @JsonProperty("startTimeUtc")
    private OffsetDateTime startTimeUtc;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "UTC")
    @JsonProperty("endTimeUtc")
    private OffsetDateTime endTimeUtc;

    private String status;          // UPCOMING | ACTIVE | EXPIRED | UNPUBLISHED | COMPLETED
    private long remainingSeconds;  // ثواني (أدق)، تقدرِ تعرضيها دقائق في الفرونت
    private String createdByUser;
    private int questionsNum;
    private boolean published;

    private int submissionId;

    public QuizDTO(
            int id,
            String title,
            String description,
            LocalDateTime startTimeLocal,   // جاي لك من DB بـ Local
            LocalDateTime endTimeLocal,
            Quiz.Status dbStatus,
            String createdByUser,
            int questionsNum,
            boolean published,
            int submissionId
    ) {
        this.id = id;
        this.title = title;
        this.description = description;

        // حوّلي من Local → UTC OffsetDateTime
        this.startTimeUtc = startTimeLocal
                .atZone(ZoneId.systemDefault())
                .withZoneSameInstant(ZoneOffset.UTC)
                .toOffsetDateTime();

        this.endTimeUtc = endTimeLocal
                .atZone(ZoneId.systemDefault())
                .withZoneSameInstant(ZoneOffset.UTC)
                .toOffsetDateTime();

        this.createdByUser = createdByUser;
        this.questionsNum = questionsNum;
        this.published = published;

        // اشتقي الحالة من الوقت + published
        this.status = deriveStatus(dbStatus, this.startTimeUtc, this.endTimeUtc, published);

        // احسبي الوقت المتبقي بـ UTC، وبالموجب فقط
        var nowUtc = OffsetDateTime.now(ZoneOffset.UTC);
        long sec = Duration.between(nowUtc, this.endTimeUtc).getSeconds();
        this.remainingSeconds = Math.max(0, sec);
        this.submissionId = submissionId;
    }

    private String mapDbStatus(Quiz.Status dbStatus) {
        if (dbStatus == null) return "UNPUBLISHED";
        return switch (dbStatus) {
            case SCHEDULED -> "UNPUBLISHED";
            case IN_PROGRESS -> "ACTIVE";
            case FINISHED -> "COMPLETED";
        };
    }

    private String deriveStatus(Quiz.Status dbStatus,
                                OffsetDateTime startUtc,
                                OffsetDateTime endUtc,
                                boolean published) {

        // لو عايزة تلتزمي بحالة DB فقط:
        // return mapDbStatus(dbStatus);

        if (!published) return "UNPUBLISHED";

        var nowUtc = OffsetDateTime.now(ZoneOffset.UTC);
        if (nowUtc.isBefore(startUtc)) return "UPCOMING";
        if (nowUtc.isAfter(endUtc)) return "EXPIRED";
        return "ACTIVE";
    }
}







