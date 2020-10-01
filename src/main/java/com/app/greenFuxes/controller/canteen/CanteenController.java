package com.app.greenFuxes.controller.canteen;

import com.app.greenFuxes.dto.canteen.CanteenSettingDTO;
import com.app.greenFuxes.dto.http.HttpResponse;
import com.app.greenFuxes.entity.user.User;
import com.app.greenFuxes.exception.user.UserNotFoundException;
import com.app.greenFuxes.service.canteen.CanteenService;
import com.app.greenFuxes.service.user.UserService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/canteen")
public class CanteenController {
  private CanteenService canteenService;
  private UserService userService;

  @Autowired
  public CanteenController(CanteenService canteenService, UserService userService) {
    this.canteenService = canteenService;
    this.userService = userService;
  }

  @PostMapping("/apply")
  ResponseEntity<?> bookCanteenPlace() throws UserNotFoundException {
    return response(HttpStatus.OK, canteenService.lunchUser(extractUser()));
  }

  @PostMapping("/finish")
  ResponseEntity<?> finishLunch() throws UserNotFoundException {
    canteenService.finishLunch(extractUser());
    return response(HttpStatus.OK, "Finishing lunch was successful!");
  }

  @GetMapping("/status")
  ResponseEntity<?> getCanteenStatus() throws UserNotFoundException {
    return new ResponseEntity<>(canteenService.canteenStatus(extractUser()), HttpStatus.OK);
  }

  @PutMapping("/configure")
  @PreAuthorize("hasAnyAuthority('admin')")
  ResponseEntity<?> configureCanteen(@RequestBody CanteenSettingDTO canteenSettingDTO)
      throws UserNotFoundException {
    canteenService.configureCanteen(extractUser(), canteenSettingDTO);
    return response(HttpStatus.OK, "Configuration was successful!");
  }

  private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String msg) {
    return new ResponseEntity<>(
        new HttpResponse(
            httpStatus.value(),
            httpStatus,
            httpStatus.getReasonPhrase().toUpperCase(),
            msg.toUpperCase()),
        httpStatus);
  }

  private User extractUser() throws UserNotFoundException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return userService.findByUsername(authentication.getName());
  }
}
