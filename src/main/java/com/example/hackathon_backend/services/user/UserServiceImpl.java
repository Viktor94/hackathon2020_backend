package com.example.hackathon_backend.services.user;


import com.example.hackathon_backend.exceptions.MissingFieldException;
import com.example.hackathon_backend.exceptions.MissingParametersException;
import com.example.hackathon_backend.exceptions.NoSuchUserException;
import com.example.hackathon_backend.exceptions.UsernameAlreadyTakenException;
import com.example.hackathon_backend.exceptions.WrongPasswordException;
import com.example.hackathon_backend.exceptions.WrongUsernameException;
import com.example.hackathon_backend.models.User;
import com.example.hackathon_backend.models.dtos.UserLoginDTO;
import com.example.hackathon_backend.models.dtos.UserRegisterDTO;
import com.example.hackathon_backend.repositories.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

  private final UserRepository userRepository;

  @Autowired
  public UserServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public User findUserByEmail(String email) {
    return userRepository.findUserByUsername(email).get();
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User temp = userRepository.findUserByUsername(username).get();
    return new org.springframework.security.core.userdetails.User(username, temp.getPassword(),
        new ArrayList<>());
  }

  @Override
  public Boolean isUserValidByEmail(String email) {
    return userRepository.findUserByUsername(email).isPresent();
  }

  @Override
  public void saveUser(User user) {
    userRepository.save(user);
  }

  @Override
  public void checkUserRegisterDTO(UserRegisterDTO userRegisterDTO)
      throws MissingFieldException, WrongUsernameException, UsernameAlreadyTakenException {

    if (userRegisterDTO == null) {
      userRegisterDTO = new UserRegisterDTO();
      List<String> nullFields = userRegisterDTO.getNullFields();
      throw new MissingFieldException(nullFields);
    }

    List<String> temp = userRegisterDTO.getNullFields();
    if (temp.size() > 0) {
      throw new MissingFieldException(temp);
    }
    if (isUserValidByEmail(userRegisterDTO.getUsername())) {
      throw new UsernameAlreadyTakenException(
          "Username already taken, please choose an other one.");
    }

    User user = new User(userRegisterDTO.getUsername(),
        passwordEncoder().encode(userRegisterDTO.getPassword()));
    saveUser(user);
  }

  @Override
  public void validatePlayerLogin(UserLoginDTO dto)
      throws MissingParametersException, NoSuchUserException, WrongPasswordException {

    if (dto == null) {
      dto = new UserLoginDTO();
      throw new MissingParametersException(dto);
    }

    if (dto.getNullFields().size() > 0) {
      throw new MissingParametersException(dto);
    }
    Optional<User> user = userRepository.findUserByUsername(dto.getUsername());

    if (user.isEmpty()) {
      throw new NoSuchUserException(dto);
    }

    if (!isEncodedPasswordMatches(dto, user.get())) {
      throw new WrongPasswordException();
    }
  }

  private Boolean isEncodedPasswordMatches(UserLoginDTO dto, User user) {
    return passwordEncoder().matches(dto.getPassword(), user.getPassword());
  }

  private PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
