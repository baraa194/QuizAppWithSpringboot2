package com.NTG.QuizAppStudentTask.Models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name="Questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Question extends AuditableEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;
    @Column(name="ques_text", length=500)
    private String  text ;

    @Column(nullable=false)
    private int Grade ;

    public enum QuestionType {
        MCQ,
        TRUE_FALSE,
        WRITTEN
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private QuestionType questionType;

    @OneToMany(mappedBy="question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Option> options;

    @ManyToOne
    @JoinColumn(name="quiz_id", nullable=false)
    @JsonBackReference
    private Quiz quiz;


    @OneToMany(mappedBy="question")
    private List<SubmissionAnswer> submissionAnswers;

}
