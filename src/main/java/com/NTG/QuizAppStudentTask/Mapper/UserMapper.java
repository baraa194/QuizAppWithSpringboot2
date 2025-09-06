package com.NTG.QuizAppStudentTask.Mapper;

import com.NTG.QuizAppStudentTask.Models.User;
import com.NTG.QuizAppStudentTask.DTO.UserDTO;
import java.util.List;
import java.util.ArrayList;

public class UserMapper {

    public static UserDTO toDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getGender(),
                user.getPhone(),
                user.getRole().getId(),
                user.getUsername(),
                user.getPassword()
        );
    }

    public static List<UserDTO> toDTOList(List<User> users) {
        List<UserDTO> dtos = new ArrayList<>();
        for (User user : users) {
            dtos.add(toDTO(user));
        }
        return dtos;
    }
}
