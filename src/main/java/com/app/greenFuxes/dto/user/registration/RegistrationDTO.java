package com.app.greenFuxes.dto.user.registration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDTO {
    private String userName;
    private String password;
    private String email;
}
