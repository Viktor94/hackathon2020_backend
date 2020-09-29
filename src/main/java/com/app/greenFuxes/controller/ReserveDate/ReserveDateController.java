package com.app.greenFuxes.controller.ReserveDate;

import com.app.greenFuxes.dto.DateDTO;
import com.app.greenFuxes.entity.user.User;
import com.app.greenFuxes.exception.user.UserNotFoundException;
import com.app.greenFuxes.service.reserveDate.ReserveDateService;
import com.app.greenFuxes.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/office")
public class ReserveDateController {

  private final ReserveDateService reserveDateService;
  private final UserService userService;

  @Autowired
  public ReserveDateController(ReserveDateService reserveDateService, UserService userService) {
    this.reserveDateService = reserveDateService;
    this.userService = userService;
  }

  @PostMapping("/reserve")
  public ResponseEntity<?> reserveDate(@RequestBody DateDTO date) throws UserNotFoundException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User user = userService.findByUsername(authentication.getName());
    reserveDateService.addUserToReserveDate(date.getDate(), user);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/users")
  public ResponseEntity<?> getUsersInOfficeOnSelectedDate(@RequestBody DateDTO dateDTO) {
    return ResponseEntity.ok(reserveDateService.findByDate(dateDTO.getDate()));
  }
}
