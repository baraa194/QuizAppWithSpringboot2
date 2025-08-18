package com.NTG.QuizAppStudentTask.Repositories;

import com.NTG.QuizAppStudentTask.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface userRepo extends JpaRepository<User,Integer> {
}
