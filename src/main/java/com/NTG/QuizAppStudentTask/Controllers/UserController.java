package com.NTG.QuizAppStudentTask.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.NTG.QuizAppStudentTask.Services.UserService;
import com.NTG.QuizAppStudentTask.DTO.UserDTO;
import com.NTG.QuizAppStudentTask.Models.User;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private  UserService userService;

    @PostMapping("/add")
    public User addUser(@RequestBody UserDTO userDTO ){
        return userService.addNewUser(userDTO);
    }



}
