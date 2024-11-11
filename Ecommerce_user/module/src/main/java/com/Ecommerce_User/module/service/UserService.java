package com.Ecommerce_User.module.service;

import com.Ecommerce_User.module.Exception.UserNotFoundException;
import com.Ecommerce_User.module.entity.User;
import com.Ecommerce_User.module.payload.UserDto;
import com.Ecommerce_User.module.repositary.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
private final ModelMapper modelMapper;
@Autowired
    public UserService(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    public UserDto addUser(UserDto userDto) {
    //convert to entity type
        User user=modelMapper.map(userDto,User.class);
        userRepository.save(user);
        UserDto dto=modelMapper.map(user, UserDto.class);
        return dto;
    }
    public UserDto getUserByUsername(String username){
    User user=userRepository.findByUsernameOrEmail(username).orElseThrow(()->new RuntimeException("user not found"));
    return modelMapper.map(user,UserDto.class);
    }

    public UserDto getUserById(String userId) {
  User user=userRepository.findById(userId)
          .orElseThrow(()->new UserNotFoundException("user not found with this id:="+userId));
  return modelMapper.map(user,UserDto.class);
    }

    public boolean existsById(String id) {
    boolean user=userRepository.existsById(id);
    return user;
    }

}
