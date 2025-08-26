package com.NTG.QuizAppStudentTask.Services;

import com.NTG.QuizAppStudentTask.DTO.UserDTO;
import com.NTG.QuizAppStudentTask.Models.Role;
import com.NTG.QuizAppStudentTask.Models.User;
import com.NTG.QuizAppStudentTask.Repositories.RoleRepo;
import com.NTG.QuizAppStudentTask.Repositories.userRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class userService {
    private final userRepo userRepo;
    private final RoleRepo roleRepo;

   // private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

//    private final userRepo userRepo;
//    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;

    // Create New User
    public UserDTO addNewUser(UserDTO userDTO) {
        Role role = roleRepo.findById(userDTO.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        // Encrypt password
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole(role);
        userRepo.save(user);
        return userDTO;
    }

    // 2- Get User by ID
    public User getUserById(int id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }


    // 5- Delete User
    public void deleteUser(int id) {
        userRepo.deleteById(id);
    }

    // 6- Change Role
    public User changeRole(int id, int roleId) {
        User existing = getUserById(id);
        Role role = roleRepo.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        existing.setRole(role);
        return userRepo.save(existing);
    }

    /*// 7- Update Status
    public User updateStatus(int id, Status status) {
        User existing = getUserById(id);
        existing.setStatus(status);
        return userRepo.save(existing);
    }*/

    //assign student
    public void assignStudentToTeacher(int studentId, int teacherId) {
        Optional<User> student = userRepo.findById(studentId);
        Optional<User> teacher = userRepo.findById(teacherId);

        if (!student.get().getRole().getRole().equalsIgnoreCase("ROLE_STUDENT")) {
            throw new RuntimeException("This user is not a student!");
        }
        if (!teacher.get().getRole().getRole().equalsIgnoreCase("ROLE_TEACHER")) {
            throw new RuntimeException("This user is not a teacher!");
        }

        student.get().setTeacher(teacher.get());
    }

}
