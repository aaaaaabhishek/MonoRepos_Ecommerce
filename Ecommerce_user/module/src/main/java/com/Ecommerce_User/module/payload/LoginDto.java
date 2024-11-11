package com.Ecommerce_User.module.payload;

import lombok.Data;

@Data
public class LoginDto {
    public String usernameOremail;
    public String password;
}
