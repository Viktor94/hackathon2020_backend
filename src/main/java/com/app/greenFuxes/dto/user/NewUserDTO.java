package com.app.greenFuxes.dto.user;

import com.app.greenFuxes.entity.user.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NewUserDTO {
    private String firstName;
    private String lastName;
    private Integer age;
    private Gender gender;
    private String userName;
    private String password;
    private String email;
    private MultipartFile profileImageUrl;
    private String role;
    private String[] authorities;
    private Boolean active;
    private Boolean notLocked;
}
