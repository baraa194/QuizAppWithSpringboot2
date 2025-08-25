package com.NTG.QuizAppStudentTask.Controllers;

import com.NTG.QuizAppStudentTask.DTO.UserDTO;
import com.NTG.QuizAppStudentTask.Models.User;
import com.NTG.QuizAppStudentTask.Services.userService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {
    @Autowired
    private userService UserService;

    @PostMapping("/add")
    public UserDTO addUser(@RequestBody UserDTO userDTO ){
        return UserService.addNewUser(userDTO);
    }

    // 5- Delete User
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable int id) {
        UserService.deleteUser(id);
    }

    // 6- Change Role
    @PatchMapping("/{id}/role")
    public User changeRole(@PathVariable int id, @RequestParam int roleId) {
        return UserService.changeRole(id, roleId);
    }

  /*  // 7- Update Status
    @PatchMapping("/{id}/status")
    public User updateStatus(@PathVariable int id, @RequestParam Status status) {
        return userService.updateStatus(id, status);
    }*/


    //assign student
    @PostMapping({"/assign"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> assignStudentToTeacher(@RequestParam int studentId, @RequestParam int teacherId) {
         UserService.assignStudentToTeacher(studentId, teacherId);
        return ResponseEntity.ok("Student with id " + studentId + " assigned to teacher " + teacherId + " successfully!");
    }
}
