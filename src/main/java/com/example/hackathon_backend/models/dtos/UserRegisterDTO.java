package com.example.hackathon_backend.models.dtos;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserRegisterDTO {

  private String username;
  private String password;

  public List<String> getNullFields() {
    List<String> nullFields = new ArrayList<>();
    for (Field f : getClass().getDeclaredFields()) {
      try {
        if (f.get(this) == null || f.get(this).equals("")) {
          nullFields.add(f.getName());
        }
      } catch (IllegalAccessException ignored) {
      }
    }
    return nullFields;
  }
}
