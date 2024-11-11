package com.AuthService.Entity;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    public String user_id;
    public String user_name;
    public String first_name;
    public String middle_name;
    public String password;
    public String email;
    private String contact;
    public LocalDateTime date;
    public String description;
    public LocalDateTime timeout;
    public LocalDateTime date_time_format;
    private Set<Role> roles;

}