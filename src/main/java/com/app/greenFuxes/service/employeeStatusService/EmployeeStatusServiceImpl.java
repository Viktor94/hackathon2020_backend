package com.app.greenFuxes.service.employeeStatusService;

import com.app.greenFuxes.entity.reservedDate.ReservedDate;
import com.app.greenFuxes.entity.user.User;
import com.app.greenFuxes.exception.user.UserNotFoundException;
import com.app.greenFuxes.security.Role;
import com.app.greenFuxes.service.reserveDate.ReserveDateService;
import com.app.greenFuxes.service.user.UserService;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeStatusServiceImpl implements EmployeeStatusService {

  private final ReserveDateService reserveDateService;
  private final UserService userService;

  @Autowired
  public EmployeeStatusServiceImpl(ReserveDateService reserveDateService, UserService userService) {
    this.reserveDateService = reserveDateService;
    this.userService = userService;
  }

  public boolean canUserEnter(Long id) throws UserNotFoundException {
    User user = userService.findById(id);
    String date = convertDateToSimpleDate();
    ReservedDate reservedDate = reserveDateService.findByDate(date);

    return reservedDate.getUsersInOffice().contains(user)
        || user.getRole().equals(Role.ROLE_VIP.toString());
  }

  private String convertDateToSimpleDate() {
    Date date1 = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    return dateFormat.format(date1);
  }
}
