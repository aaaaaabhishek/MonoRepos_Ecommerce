package com.Ecommerce_User.module.payload;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private String user_id;
    @NotNull
    private String user_name;
    @Column(nullable = false)
    private String User_type;
    @Column(nullable = false)
    private String address;
    @Email
    private String email;
    @NotNull(message = "Mobile number is required")
    @Digits(integer = 10, fraction = 0, message = "Mobile number must be a 10-digit number")
    private Long mobile_no;
}
