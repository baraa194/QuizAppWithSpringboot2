package com.NTG.QuizAppStudentTask.Controllers;

import com.NTG.QuizAppStudentTask.DTO.UserDTO;
import com.NTG.QuizAppStudentTask.Models.User;
import com.NTG.QuizAppStudentTask.Services.userService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:8081")
@RequestMapping("/admin")
@PreAuthorize("hasRole( 'ADMIN')")
public class UserController {
    @Autowired
    private userService userService;

    @GetMapping("/teachers")
    public List<UserDTO> getTeachers() {
        return userService.getUsersByRole(2);
    }

    @GetMapping("/students")
    public List<UserDTO> getStudents() {
        return userService.getUsersByRole(3);
    }

    @PostMapping("/add")
    public UserDTO addUser(@RequestBody UserDTO userDTO ){
        return userService.addNewUser(userDTO);
    }



    // 5- Delete User
    @DeleteMapping("delete/{id}")
    public void deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
    }

    // 6- Change Role
    @PatchMapping("/{id}/role")
    public User changeRole(@PathVariable int id, @RequestParam int roleId) {
        return userService.changeRole(id, roleId);
    }

  /*  // 7- Update Status
    @PatchMapping("/{id}/status")
    public User updateStatus(@PathVariable int id, @RequestParam Status status) {
        return userService.updateStatus(id, status);
    }*/


    @PutMapping("/update/{id}")
    public UserDTO updateUserInfo(@PathVariable int id,@RequestBody UserDTO updatedUser){
        return userService.updateUserInfo(id,updatedUser);
    }



}
