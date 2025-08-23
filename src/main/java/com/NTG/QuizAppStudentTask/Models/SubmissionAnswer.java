package com.NTG.QuizAppStudentTask.Models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="SubmissionAnswers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionAnswer extends  AuditableEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int Id;
    @Column(name="student_answer",nullable = false,length = 500)
    private String studentAnswer ;
    @Column(nullable = false)
    private boolean isCorrect ;
    @Column(nullable = false)
    private float manualGrade ;


    @ManyToOne
    @JoinColumn(name="submission_id", nullable=false)
    @JsonBackReference
    private Submission submission;


    @ManyToOne
    @JoinColumn(name="question_id", nullable=false)
    private Question question;



}
