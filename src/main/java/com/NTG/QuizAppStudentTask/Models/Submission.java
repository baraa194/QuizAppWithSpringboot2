package com.NTG.QuizAppStudentTask.Models;

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
public class Submission extends AuditableEntity{
    public Submission() {
        this.submittedAt = LocalDateTime.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;
    @Column(nullable = false)
    private LocalDateTime submittedAt ;
    @Column(nullable = false)
    private float totalGrade=0;

    @ManyToOne
    @JoinColumn(name="student_id", nullable=false)
    private User student;


    @ManyToOne
    @JoinColumn(name="quiz_id", nullable=false)
    private Quiz quiz;


    @OneToMany(mappedBy="submission", cascade=CascadeType.ALL)
    private List<SubmissionAnswer> submissionAnswers;




}
