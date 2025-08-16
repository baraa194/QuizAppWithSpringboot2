package com.NTG.QuizAppStudentTask.Models;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name="Users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends AuditableEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;
    @Column(nullable = false,length = 100)
    private String name;
    @Column(nullable = false,length = 100,unique = true)
    private String email;
    @Column(nullable = false,length = 100)
    private String password;
    @Column(nullable = false,length = 100,unique = true)
    private String username;


    @OneToMany(mappedBy = "createdByUser")
    private List<Quiz> quizzes;


    @OneToMany(mappedBy = "student")
    private List<Submission> submissions;

    @ManyToOne
    @JoinColumn(name="role_id", nullable=false)
    private Role role;







}
