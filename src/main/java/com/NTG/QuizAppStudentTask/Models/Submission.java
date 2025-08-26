package com.NTG.QuizAppStudentTask.Models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="Submissions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Submission extends AuditableEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;
    private LocalDateTime submittedAt ;
    @Column(nullable = false)
    private float totalGrade=0;
    @Column(nullable = false)
    private boolean autoSubmitted = false;
    @Column(nullable = false)
    private LocalDateTime startedAt;


    @Column(nullable = false)
    private LocalDateTime deadline;


    public enum Status
    {
        IN_PROGRESS,
        COMPLETED,

    }
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Submission.Status status;

    @ManyToOne
    @JoinColumn(name="student_id", nullable=false)
    private User student;


    @ManyToOne
    @JsonBackReference
    @JoinColumn(name="quiz_id", nullable=false)
    private Quiz quiz;


    @OneToMany(mappedBy="submission", cascade=CascadeType.ALL)
    @JsonManagedReference
    private List<SubmissionAnswer> submissionAnswers;




}
