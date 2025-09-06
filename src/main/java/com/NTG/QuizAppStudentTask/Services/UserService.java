package com.NTG.QuizAppStudentTask.Services;

import com.NTG.QuizAppStudentTask.DTO.UserDTO;
import com.NTG.QuizAppStudentTask.Models.Role;
import com.NTG.QuizAppStudentTask.Models.User;
import com.NTG.QuizAppStudentTask.Mapper.UserMapper;
import com.NTG.QuizAppStudentTask.Repositories.RoleRepo;
import com.NTG.QuizAppStudentTask.Repositories.userRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
@RequiredArgsConstructor
public class userService {
//    private final userRepo userRepo;
//    private final RoleRepo roleRepo;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final userRepo userRepo;
    private final RoleRepo roleRepo;
   // private final PasswordEncoder passwordEncoder;

    // Create New User
    public UserDTO addNewUser(UserDTO userDTO) {
        Role role = roleRepo.findById(userDTO.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        User user = new User();
        user.setUsername(userDTO.getUserName());
        user.setPassword(userDTO.getPassword());
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setGender(userDTO.getGender());
        user.setPhone(userDTO.getPhone());
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
        User user =userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if(!user.getIsDeleted()){
            user.setIsDeleted(true);
            userRepo.save(user);
        }
        else
            throw new RuntimeException("this user already deleted");

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
    public List<UserDTO> getUsersByRole(int id){
        List<User> users = userRepo.findByRole_Id(id);
        users =users.stream().filter(x -> x.getIsDeleted()==false).toList();
        return UserMapper.toDTOList(users);
    }

    public UserDTO updateUserInfo(int id,UserDTO dto){
      User user=userRepo.findById(id).orElseThrow(() -> new RuntimeException("user not found"));
      user.setName(dto.getName());
      user.setUsername(dto.getUserName());
      String hashed = passwordEncoder.encode(dto.getPassword());
      user.setPassword(hashed);
      user.setGender(dto.getGender());
      user.setEmail(dto.getEmail());
      userRepo.save(user);
      return UserMapper.toDTO(user);
   }
}
