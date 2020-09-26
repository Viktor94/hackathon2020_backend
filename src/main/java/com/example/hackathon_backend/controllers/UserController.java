package com.example.hackathon_backend.controllers;

import com.example.hackathon_backend.exceptions.MissingFieldException;
import com.example.hackathon_backend.exceptions.MissingParametersException;
import com.example.hackathon_backend.exceptions.NoSuchUserException;
import com.example.hackathon_backend.exceptions.UsernameAlreadyTakenException;
import com.example.hackathon_backend.exceptions.WrongPasswordException;
import com.example.hackathon_backend.exceptions.WrongUsernameException;
import com.example.hackathon_backend.models.User;
import com.example.hackathon_backend.models.dtos.ResponseDTO;
import com.example.hackathon_backend.models.dtos.UserLoginDTO;
import com.example.hackathon_backend.models.dtos.UserRegisterDTO;
import com.example.hackathon_backend.models.dtos.UserRegisterResponseDTO;
import com.example.hackathon_backend.security.JwtUtil;
import com.example.hackathon_backend.services.user.UserService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user-management")
public class UserController {

  private final JwtUtil jwtUtil;
  private final UserService userService;
  private final Logger LOGGER;

  @Autowired
  public UserController(JwtUtil jwtUtil, UserService userService, Logger logger) {
    this.jwtUtil = jwtUtil;
    this.userService = userService;
    this.LOGGER = logger;
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody(required = false) UserRegisterDTO userRegisterDTO)
      throws WrongUsernameException, MissingFieldException, UsernameAlreadyTakenException {
    userService.checkUserRegisterDTO(userRegisterDTO);
    User user = userService.findUserByEmail(userRegisterDTO.getUsername());
    LOGGER.info("@POST /user-management/register " + user.getUsername() + " has registered!");
    UserRegisterResponseDTO dto = new UserRegisterResponseDTO(user.getUsername());

    return new ResponseEntity<>(dto, HttpStatus.OK);
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody(required = false) UserLoginDTO dto)
      throws NoSuchUserException, MissingParametersException, WrongPasswordException {
    LOGGER.info("@POST /user-management/login " + dto.getUsername() + " has signed in!");
    userService.validatePlayerLogin(dto);
    final UserDetails userDetails = userService.loadUserByUsername(dto.getUsername());
    final String jwt = jwtUtil.generateToken(userDetails, 60);
    ResponseDTO responseDTO = new ResponseDTO(jwt);

    return new ResponseEntity<>(responseDTO, HttpStatus.OK);
  }
}
