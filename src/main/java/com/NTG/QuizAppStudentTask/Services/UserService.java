package com.NTG.QuizAppStudentTask.Services;

import com.NTG.QuizAppStudentTask.Repositories.QuizRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.NTG.QuizAppStudentTask.Repositories.userRepo;
import com.NTG.QuizAppStudentTask.Repositories.roleRepo;
import com.NTG.QuizAppStudentTask.DTO.UserDTO;
import com.NTG.QuizAppStudentTask.Models.User;
import com.NTG.QuizAppStudentTask.Models.Role;



@Service
@RequiredArgsConstructor
public class UserService {
    private final userRepo UserRepo;
    private final roleRepo RoleRepo;
    public User addNewUser(UserDTO userDTO){

        Role role = RoleRepo.findById(userDTO.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found"));
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setRole(role);
        return UserRepo.save(user);
    }
}
