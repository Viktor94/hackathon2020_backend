package com.app.greenFuxes.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
  private Long id;
  private String userName;
  private String email;
  private String firstName;
  private String lastName;
  private String role;
  private String[] authorities;
  private Boolean active;
  private Boolean notLocked;
  private String profileImageUrl;
}
