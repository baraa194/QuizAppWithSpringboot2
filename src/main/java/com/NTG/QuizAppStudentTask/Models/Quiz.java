package com.NTG.QuizAppStudentTask.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="Quizzes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Quiz extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;
    @Column(nullable = false, length = 10, unique = true)
    private String title ;
    @Column( length = 500)
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime ;

    public enum Status {
        SCHEDULED,
        IN_PROGRESS,
        FINISHED
    };

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;

    @ManyToOne
    @JoinColumn(name="created_by_user_id", nullable=false)
    private User createdByUser;


    @OneToMany(mappedBy="quiz", cascade=CascadeType.ALL)
    private List<Question> questions;


    @OneToMany(mappedBy="quiz")
    private List<Submission> submissions;

}
