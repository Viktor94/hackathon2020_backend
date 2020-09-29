package com.app.greenFuxes.service.employeeStatusService;

import com.app.greenFuxes.exception.user.UserNotFoundException;

public interface EmployeeStatusService {
  boolean canUserEnter(Long id) throws UserNotFoundException;
}
