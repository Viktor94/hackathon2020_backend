package com.app.greenFuxes.dto.office;

import com.app.greenFuxes.entity.user.User;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsersInOfficeDTO {

  private List<User> usersInOffice;
  private Integer numberOfFreeSpots;
  private Integer officeCapacity;
}
