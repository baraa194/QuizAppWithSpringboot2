package com.NTG.QuizAppStudentTask.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="SystemSettings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SystemSetting extends AuditableEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;
    @Column(name = "default_quiz_time", nullable = false)
   private int  defaultQuiztime ;
    @Column(name = "max_attempts", nullable = false)
    private int maxAttempts ;


}
