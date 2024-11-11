package com.Ecommerce_User.module.controller;

import com.Ecommerce_User.module.payload.UserDto;
import com.Ecommerce_User.module.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    @Autowired
    public UserController(UserService userService){
        this.userService=userService;
    }
    @PostMapping
    public ResponseEntity<?> addUSer(@Valid @RequestBody UserDto userDto, BindingResult result) {
        if (result.hasErrors()) {
            String errorMessage = result.getFieldError() != null ? result.getFieldError().getDefaultMessage() : "Invalid input";

            return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
        }
        UserDto user = userService.addUser(userDto);
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        return new ResponseEntity<>(userDto, HttpStatus.BAD_REQUEST);
    }
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") String userId){
        UserDto userDto=userService.getUserById(userId);
            if(userDto!=null)
                return new ResponseEntity<>(userDto,HttpStatus.OK);
            return new ResponseEntity<>(userDto,HttpStatus.BAD_REQUEST);
         }

    @GetMapping("users/username/{username}")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable String username) {
        try {
            UserDto userDto = userService.getUserByUsername(username);
            if (userDto != null) {
                return new ResponseEntity<>(userDto, HttpStatus.OK);
            } else {
                // Return NOT_FOUND if user is not found
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
            }
        } catch (Exception e) {
            // Log the error and return an internal server error response
            // You can use a logging framework like SLF4J or Log4j here
            System.err.println("Error occurred while fetching user: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
    }
   /* Checking Resource Existence:
    You can use HEAD to determine if a resource exists without fetching
    the entire resource body. This is especially useful for large
    files or data where you only want to verify existence.*/
   @RequestMapping(value = "/{id}", method = RequestMethod.HEAD)
   public ResponseEntity<Void> checkUserExists(@PathVariable String id) {
       if (userService.existsById(id)) {
           return ResponseEntity.ok().build();
       }
       return ResponseEntity.notFound().build();
   }
    @RequestMapping(value = "/users", method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> options() {
        return ResponseEntity.ok()
                .allow(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE)
                .build();
        //output:=Allow: GET, POST, OPTIONS, PUT, DELETE
        //Discovering the allowed HTTP methods for a resource.
    }



}

