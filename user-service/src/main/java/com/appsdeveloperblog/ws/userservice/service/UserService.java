package com.appsdeveloperblog.ws.userservice.service;

import com.appsdeveloperblog.ws.userservice.dto.UserDto;
import com.appsdeveloperblog.ws.userservice.entity.User;
import com.appsdeveloperblog.ws.userservice.mapper.UserMapper;
import com.appsdeveloperblog.ws.userservice.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository ;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto crateUser (UserDto userDto){
        User user = UserMapper.toEntity(userDto);
        return UserMapper.toDTO(userRepository.save(user));
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("user not found"));
        return UserMapper.toDTO(user);
    }

    public UserDto updatedUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("user not found"));
        UserMapper.updated(userDto, user);
        return UserMapper.toDTO(user);
    }

    public void deletedUser(Long id){
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("user not found"));
        userRepository.delete(user);
    }

}
