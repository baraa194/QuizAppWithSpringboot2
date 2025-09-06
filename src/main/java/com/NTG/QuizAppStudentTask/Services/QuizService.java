package com.NTG.QuizAppStudentTask.Services;
import com.NTG.QuizAppStudentTask.DTO.*;
import com.NTG.QuizAppStudentTask.Models.*;
import com.NTG.QuizAppStudentTask.QuizStatusMapper;
import com.NTG.QuizAppStudentTask.Repositories.QuizRepo;
import com.NTG.QuizAppStudentTask.Repositories.SubmissionRepo;
import com.NTG.QuizAppStudentTask.Repositories.questionRepo;
import com.NTG.QuizAppStudentTask.Repositories.userRepo;
import com.NTG.QuizAppStudentTask.Config.AuditorAwareImpl;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizService {
    private static final Logger log = LoggerFactory.getLogger(QuizService.class);

    private final QuizRepo quizRepo;
    private final userRepo userrepo;
    private final questionRepo questionRepo;
    private final AuditorAwareImpl auditorAwareImpl;
    private final SubmissionRepo submissionRepo;

    // ===== Helpers عامة =====
    private String deriveStatusForStudent(
            Submission s,
            OffsetDateTime startUtc,
            OffsetDateTime endUtc,
            boolean published,
            OffsetDateTime nowUtc
    ) {
        // لو مش منشور أصلاً
        if (!published) return "expired";

        // لو الطالب سلّم
        if (s.getStatus() == Submission.Status.COMPLETED) return "finished";

        // لو عدّى الديدلاين الخاص بالطالب (لو موجود)
        if (s.getDeadline() != null && nowUtc.isAfter(s.getDeadline().atOffset(ZoneOffset.UTC))) {
            return "expired";
        }

        // لو عدّى وقت الكويز العام
        if (nowUtc.isAfter(endUtc)) return "expired";

        // قبل البداية
        if (nowUtc.isBefore(startUtc)) return "scheduled";

        // أثناء الوقت ولم يُكمل
        return "active";
    }

    private float computeTotalPoints(Quiz q) {
        if (q.getQuestions() == null || q.getQuestions().isEmpty()) return 0f;
        float sum = 0f;
        for (var qq : q.getQuestions()) {
            float g = qq.getGrade();
            if (g <= 0f) g = 1f;       // نفس الديفولت المستخدم في الـMCQ
            sum += g;
        }
        return sum;
    }

    private OffsetDateTime toUtc(LocalDateTime local) {
        if (local == null) return null;
        // ⬇️ Treat stored LocalDateTime as UTC (column holds UTC-without-offset)
        // Avoid converting from systemDefault(); just attach UTC offset.
        return local.atOffset(ZoneOffset.UTC);
    }

    private LocalDateTime utcToLocalDateTime(OffsetDateTime utc) {
        if (utc == null) return null;
        // نخزّن في الـ DB كـ UTC داخل LocalDateTime (بدون أوفست)
        return utc.atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
    }

    private String deriveStatusForUI(OffsetDateTime startUtc,
                                     OffsetDateTime endUtc,
                                     boolean published) {
        if (!published) return "UNPUBLISHED";
        OffsetDateTime nowUtc = OffsetDateTime.now(ZoneOffset.UTC);
        if (nowUtc.isBefore(startUtc)) return "UPCOMING";
        if (nowUtc.isAfter(endUtc))   return "EXPIRED";
        return "ACTIVE";
    }

    public List<QuizDTO> getAllForStudents() {
        // 1) هات الطالب الحالي
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new RuntimeException("User not authenticated");
        }
        var username = auth.getName();
        var student = userrepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2) هات Submissions الطالب ومعاها الكويز
        List<Submission> subs = submissionRepo.findAllByStudentIdWithQuiz(student.getId());

        OffsetDateTime nowUtc = OffsetDateTime.now(ZoneOffset.UTC);

        // 3) خرّجي DTOs مصمّمة للطالب
        return subs.stream().map(s -> {
            Quiz q = s.getQuiz();

            OffsetDateTime startUtc = toUtc(q.getStartTime());
            OffsetDateTime endUtc   = toUtc(q.getEndTime());

            QuizDTO dto = new QuizDTO();
            dto.setId(q.getId());
            dto.setTitle(q.getTitle());
            dto.setDescription(q.getDescription());

            // أوقات موحّدة (UTC) للفرونت
            dto.setStartTimeUtc(startUtc);
            dto.setEndTimeUtc(endUtc);

            // بيانات العرض
            dto.setCreatedByUser(q.getCreatedByUser() != null ? q.getCreatedByUser().getName() : null);
            dto.setQuestionsNum(q.getQuestions() != null ? q.getQuestions().size() : 0);
            dto.setPublished(q.isPublished());

            // حالة الطالب (مش حالة الكويز العامة)
            String status = deriveStatusForStudent(s, startUtc, endUtc, q.isPublished(), nowUtc);
            dto.setStatus(status);

            long remaining = Duration.between(nowUtc, endUtc).getSeconds();
            dto.setRemainingSeconds(Math.max(0, remaining));

            // روابط تخص الطالب (مهمة للداشبورد و review)
            dto.setSubmissionId(s.getId());

            // لو عايزة، ممكن تبعتي إجمالي الدرجة ونقط الكويز (مش ضروري للطلب الحالي)
            // dto.setTotalGrade( s.getTotalGrade() != null ? s.getTotalGrade() : 0f );
            // dto.setTotalPoints( computeTotalPoints(q) );

            return dto;
        }).collect(Collectors.toList());
    }




    public List<QuizResultDTO>  getAllResults() {
        return quizRepo.findStudentQuizResults();
    }

   /* public QuizDTO getteacherQuizById(int id, int teacherId) {
        Optional<QuizDTO> quiz = quizRepo.findByIdAndTeacherId(id, teacherId);
        if (quiz.isPresent()) {
            return quiz.get();
        } else {
            throw new RuntimeException("Quiz not found");
        }

    }*/

    //create quiz by teacher and admin
   /* public QuizDTO createQuiz(QuizDTO quiz){


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
    }*/

    // ===================== Create Quiz =====================

    public void createQuizWithQuestions(QuizWithquestionsDTO dto) {

        log.info("📥 createQuizWithQuestions called");
        log.debug("Payload DTO: {}", dto);

        // 1) المستخدم الحالي
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();
        String username = (principal instanceof UserDetails)
                ? ((UserDetails) principal).getUsername()
                : principal.toString();

        User user = userrepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2) التوقيت (لا تحويل ساعات هنا)
        if (dto.getStartTimeUtc() == null)
            throw new RuntimeException("startTimeUtc is required (UTC ISO string)");
        if (dto.getDurationMinutes() == null)
            throw new RuntimeException("durationMinutes is required");

        // قيمة UTC جاية من الـDTO
        OffsetDateTime startUtc = dto.getStartTimeUtc();
        OffsetDateTime endUtc = startUtc.plusMinutes(dto.getDurationMinutes());

        // ✅ نخزّن في الـDB LocalDateTime لكن "كما هي" UTC (بدون أي shift)
        LocalDateTime startToPersist = LocalDateTime.ofInstant(startUtc.toInstant(), ZoneOffset.UTC);
        LocalDateTime endToPersist = LocalDateTime.ofInstant(endUtc.toInstant(), ZoneOffset.UTC);

        // 3) إنشاء الـQuiz
        Quiz quiz = new Quiz();
        quiz.setTitle(dto.getTitle());
        quiz.setDescription(dto.getDescription());

        // أعمدة الـEntity عندك هي LocalDateTime
        quiz.setStartTime(startToPersist);
        quiz.setEndTime(endToPersist);

        quiz.setPublished(false);
        quiz.setStatus(Quiz.Status.SCHEDULED);
        quiz.setCreatedByUser(user);
        quizRepo.save(quiz);

        // 4) الأسئلة والاختيارات (كما هي عندك)
        if (dto.getQuestions() != null) {
            for (QuestionDTO qDto : dto.getQuestions()) {
                Question question = new Question();
                question.setText(qDto.getText());
                question.setGrade(qDto.getGrade());
                question.setQuestionType(Question.QuestionType.valueOf(qDto.getType().toUpperCase()));
                question.setQuiz(quiz);

                List<Option> options = (qDto.getOptions() != null)
                        ? qDto.getOptions().stream().map(optDto -> {
                    Option opt = new Option();
                    opt.setAnswer(optDto.getAnswer());
                    opt.setCorrect(optDto.isCorrect());
                    opt.setQuestion(question);
                    return opt;
                }).collect(java.util.stream.Collectors.toList())
                        : java.util.Collections.emptyList();

                question.setOptions(options);
                question.setModelAnswer(qDto.getModelAnswer());
                questionRepo.save(question);
            }
        }
    }



    // ===================== Teacher: Get All Quizzes =====================
    // ===== 3) لعرض المدرّس =====
    public List<QuizDTO> getQuizzesForCurrentTeacher() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();
        String username = (principal instanceof UserDetails)
                ? ((UserDetails) principal).getUsername()
                : principal.toString();

        User user = userrepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Quiz> quizzes = quizRepo.findByCreatedByUser(user);

        return quizzes.stream().map(quiz -> {
            OffsetDateTime startUtc = toUtc(quiz.getStartTime());
            OffsetDateTime endUtc   = toUtc(quiz.getEndTime());
            OffsetDateTime nowUtc   = OffsetDateTime.now(ZoneOffset.UTC);

            QuizDTO dto = new QuizDTO();
            dto.setId(quiz.getId());
            dto.setTitle(quiz.getTitle());
            dto.setDescription(quiz.getDescription());

            dto.setStartTimeUtc(startUtc);
            dto.setEndTimeUtc(endUtc);

            dto.setCreatedByUser(user.getName());
            dto.setQuestionsNum(quiz.getQuestions() != null ? quiz.getQuestions().size() : 0);
            dto.setPublished(quiz.isPublished());

            // حالة واجهة المدرس: تعتمد على النشر + الوقت
            dto.setStatus(deriveStatusForUI(startUtc, endUtc, quiz.isPublished()));

            long remaining = Duration.between(nowUtc, endUtc).getSeconds();
            dto.setRemainingSeconds(Math.max(0, remaining));

            return dto;
        }).collect(Collectors.toList());
    }
    /** Helper: يحدّد الحالة الظاهرة في Dashboard المدرّس */
    private String toTeacherUiStatus(LocalDateTime start, LocalDateTime end, boolean published, LocalDateTime now) {
        if (!published) return "UNPUBLISHED";
        if (end != null && now.isAfter(end)) return "COMPLETED";
        // Published ولسّه داخل المدة (أو قبل البداية) => نعرّضها Active على الـ UI
        return "ACTIVE";
    }


    @Transactional
    public void publishQuiz(int id) {
        Quiz q = quizRepo.findById(id).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Quiz not found: " + id));

        q.setPublished(true);

        quizRepo.save(q);
    }

    @Transactional
    public void unpublishQuiz(int id) {
        Quiz q = quizRepo.findById(id).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Quiz not found: " + id));
        q.setPublished(false);

        quizRepo.save(q);
    }





    @Transactional
    public void updateQuizWithQuestions(int quizId, QuizWithquestionsDTO dto) {
        // 0) هات الكويز
        Quiz quiz = quizRepo.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        // (اختياري) تحقّق الملكية
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();
        String username = (principal instanceof UserDetails)
                ? ((UserDetails) principal).getUsername()
                : principal.toString();

        User user = userrepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // لو حابة تمنعي غير المالك:
        // if (!quiz.getCreatedByUser().getId().equals(user.getId())) {
        //     throw new RuntimeException("You are not allowed to edit this quiz");
        // }

        // 1) التوقيت من الـ DTO (UTC ISO)
        if (dto.getStartTimeUtc() == null) {
            throw new RuntimeException("startTimeUtc is required (UTC ISO)");
        }
        if (dto.getDurationMinutes() == null) {
            throw new RuntimeException("durationMinutes is required");
        }

        OffsetDateTime startUtc = dto.getStartTimeUtc();                 // e.g. 2025-09-01T16:22:00Z
        OffsetDateTime endUtc   = startUtc.plusMinutes(dto.getDurationMinutes());

        // ✨ نخزّن في الـ DB كـ UTC داخل LocalDateTime لتفادي فروق المناطق
        LocalDateTime startToPersist = startUtc.atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
        LocalDateTime endToPersist   = endUtc.atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();

        // 2) تعديل بيانات الكويز الأساسية
        quiz.setTitle(dto.getTitle());
        quiz.setDescription(dto.getDescription());
        quiz.setStartTime(startToPersist);
        quiz.setEndTime(endToPersist);
        // مهم: لا نغيّر published هنا

        // 3) حالة DB (اختياري لو لسه مستخدمين enum داخلي)
        if (dto.getStatus() != null && !dto.getStatus().isBlank()) {
            Quiz.Status dbStatus = switch (dto.getStatus().toUpperCase()) {
                case "UNPUBLISHED" -> Quiz.Status.SCHEDULED;
                case "ACTIVE"      -> Quiz.Status.IN_PROGRESS;
                case "COMPLETED"   -> Quiz.Status.FINISHED;
                default            -> Quiz.Status.SCHEDULED;
            };
            quiz.setStatus(dbStatus);
        }

        quiz.setCreatedByUser(quiz.getCreatedByUser() == null ? user : quiz.getCreatedByUser());
        quizRepo.save(quiz);

        // 4) امسح الأسئلة القديمة (لو عندك orphanRemoval = true/CASCADE.ALL على OneToMany ممكن تكتفي بـ clear)
        if (quiz.getQuestions() != null && !quiz.getQuestions().isEmpty()) {
            questionRepo.deleteAll(quiz.getQuestions());
            quiz.getQuestions().clear();
        }

        // 5) أعد إنشاء الأسئلة والاختيارات
        if (dto.getQuestions() != null) {
            for (QuestionDTO qDto : dto.getQuestions()) {
                Question question = new Question();
                question.setText(qDto.getText());
                question.setGrade(qDto.getGrade());
                question.setQuestionType(Question.QuestionType.valueOf(qDto.getType().toUpperCase()));
                question.setQuiz(quiz);

                List<Option> options = (qDto.getOptions() != null)
                        ? qDto.getOptions().stream().map(optDto -> {
                    Option opt = new Option();
                    opt.setAnswer(optDto.getAnswer());
                    opt.setCorrect(optDto.isCorrect());
                    opt.setQuestion(question);
                    return opt;
                }).collect(Collectors.toList())
                        : Collections.emptyList();

                question.setOptions(options);
                question.setModelAnswer(qDto.getModelAnswer());
                questionRepo.save(question);
            }
        }
    }



    //teacher delete in quiz by id
    public void deleteQuiz(int id) {
        Quiz quiz = quizRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        quizRepo.delete(quiz);

    }
    @Transactional
    public QuizWithquestionsDTO getQuizWithQuestionsById(int quizId) {
        // جلب الكويز
        Quiz quiz = quizRepo.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        // ⏱️ تحويل التوقيتات المخزنة (LocalDateTime كقيمة UTC) إلى OffsetDateTime UTC
        OffsetDateTime startUtc = toUtc(quiz.getStartTime());
        OffsetDateTime endUtcDb = toUtc(quiz.getEndTime());

        // durationMinutes = end - start بالدقايق (Long)
        Long durationMinutes = null;
        if (quiz.getStartTime() != null && quiz.getEndTime() != null) {
            durationMinutes = Math.max(
                    0L,
                    Duration.between(quiz.getStartTime(), quiz.getEndTime()).toMinutes()
            );
        }

        // لو مفيش end بقا، استخدم start + duration لما يكون duration موجود
        OffsetDateTime endUtc =
                (startUtc != null && durationMinutes != null)
                        ? startUtc.plusMinutes(durationMinutes)
                        : endUtcDb;

        // نفس اشتقاق الحالة المستخدم في getQuizzesForCurrentTeacher
        String uiStatus = deriveStatusForUI(startUtc, endUtc, quiz.isPublished());

        // الأسئلة والاختيارات → DTOs
        List<QuestionDTO> questionDTOs =
                (quiz.getQuestions() == null ? List.<QuestionDTO>of()
                        : quiz.getQuestions().stream().map(q -> {
                    QuestionDTO qdto = new QuestionDTO();
                    qdto.setText(q.getText());
                    qdto.setType(q.getQuestionType() != null ? q.getQuestionType().name() : null); // "MCQ"/"WRITTEN"
                    qdto.setGrade(q.getGrade());
                    qdto.setModelAnswer(q.getModelAnswer());

                    List<OptionDTO> opts =
                            (q.getOptions() == null ? List.<OptionDTO>of()
                                    : q.getOptions().stream()
                                    .map(o -> new OptionDTO(o.isCorrect(), o.getAnswer()))
                                    .toList());
                    qdto.setOptions(opts);
                    return qdto;
                }).toList());

        // بناء الـDTO بالشكل المطلوب (من غير حقول id/published/…)
        QuizWithquestionsDTO dto = new QuizWithquestionsDTO();

        dto.setTitle(quiz.getTitle());
        dto.setDescription(quiz.getDescription());
        dto.setStartTimeUtc(startUtc);
        dto.setDurationMinutes(durationMinutes); // الـgetter بتاع endTimeUtc هيحسب النهاية
        dto.setStatus(uiStatus);
        dto.setQuestions(questionDTOs);
        dto.setModelAnswer(null); // مفيش modelAnswer على مستوى الكويز عادةً

        return dto;
    }



}



