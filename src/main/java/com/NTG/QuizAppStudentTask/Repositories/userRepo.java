package com.NTG.QuizAppStudentTask.Repositories;

import com.NTG.QuizAppStudentTask.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface userRepo extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    List<User> findByRole_Id(int roleId);
}
