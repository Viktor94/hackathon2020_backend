package com.app.greenFuxes.service.user;

import com.app.greenFuxes.dto.user.NewUserDTO;
import com.app.greenFuxes.dto.user.registration.RegistrationDTO;
import com.app.greenFuxes.entity.user.User;
import com.app.greenFuxes.exception.user.EmailNotFoundException;
import com.app.greenFuxes.exception.user.UserManipulationException;
import com.app.greenFuxes.exception.user.UserNotFoundException;
import java.io.IOException;
import java.util.List;

public interface UserService {

  User register(RegistrationDTO registrationDTO) throws UserManipulationException;

  List<User> getUsers();

  User findByUsername(String username) throws UserNotFoundException;

  User findByEmail(String email) throws EmailNotFoundException;

  User addUser(NewUserDTO newUserDTO) throws UserManipulationException, IOException;

  User findById(Long id) throws UserNotFoundException;

  void deleteUser(Long id) throws UserNotFoundException;

  void activateUser(Long id) throws UserNotFoundException;

  String getUserImageUrl(Long id) throws UserNotFoundException;
}
