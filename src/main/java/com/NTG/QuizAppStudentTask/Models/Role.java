package com.NTG.QuizAppStudentTask.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name="Roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;
    @Column(nullable = false,length = 50)
    private String role;

    @OneToMany(mappedBy="role")
    private List<User> users;
}
