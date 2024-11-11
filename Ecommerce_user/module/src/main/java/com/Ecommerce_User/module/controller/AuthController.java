package com.Ecommerce_User.module.controller;
import com.Ecommerce_User.module.entity.JWTAuthResponse;
import com.Ecommerce_User.module.entity.User;
import com.Ecommerce_User.module.payload.LoginDto;
import com.Ecommerce_User.module.payload.UserDto;
import com.Ecommerce_User.module.repositary.UserRepository;
import com.Ecommerce_User.module.security.JwtTokenProvider;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class AuthController {
      public UserRepository userRepository;
    public ModelMapper mapper;
    public AuthenticationManager authenticationManager;
    public JwtTokenProvider tokenProvider;
    @Autowired
    public AuthController(UserRepository userRepository, ModelMapper mapper,AuthenticationManager authenticationManager,JwtTokenProvider tokenProvider){
        this.mapper=mapper;
        this.userRepository=userRepository;
        this.authenticationManager=authenticationManager;
        this.tokenProvider=tokenProvider;

    }
    @PostMapping("/signin")
    public ResponseEntity<JWTAuthResponse> authenticateuser(@RequestBody LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsernameOremail(), loginDto.getPassword())
        );//authenticate method compare actual value with expected value
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //get token from tokenProvider
        String token=tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JWTAuthResponse(token));
    }
    @PostMapping("/register")
    public ResponseEntity<?> creteaccount(@RequestBody UserDto userDto){
        User user=mapper.map(userDto, User.class);
        if(userRepository.findByUsername(user.getUser_name())){
            return new  ResponseEntity("User is Alredy presentesd", HttpStatus.BAD_REQUEST);
        }
        if(userRepository.findByEmail(user.getEmail())){
            return new  ResponseEntity("Email is alredy presneted", HttpStatus.BAD_REQUEST);
        }
        userRepository.save(user);
        return new ResponseEntity<>("user is Register ",HttpStatus.CREATED);
    }
}
