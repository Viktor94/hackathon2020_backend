package com.app.greenFuxes.controller.entrance;

import com.app.greenFuxes.exception.user.UserNotFoundException;
import com.app.greenFuxes.service.employeeStatusService.EmployeeStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/employee-status")
public class EntranceController {

  private final EmployeeStatusService employeeStatusService;

  @Autowired
  public EntranceController(EmployeeStatusService employeeStatusService) {
    this.employeeStatusService = employeeStatusService;
  }

  @PostMapping("/enter/{id}")
  public ResponseEntity<?> entry(@PathVariable Long id) throws UserNotFoundException {
    if (employeeStatusService.canUserEnter(id)) {
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/exit")
  public ResponseEntity<?> exit() {
    // TODO Create exit endpoint when hourly reservation is available!
    return ResponseEntity.ok().build();
  }
}
