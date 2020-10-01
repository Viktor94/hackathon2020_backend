package com.app.greenFuxes.service.user;

import com.app.greenFuxes.dto.user.NewUserDTO;
import com.app.greenFuxes.dto.user.UserProfileDTO;
import com.app.greenFuxes.dto.user.registration.RegistrationDTO;
import com.app.greenFuxes.entity.user.User;
import com.app.greenFuxes.exception.user.EmailExistException;
import com.app.greenFuxes.exception.user.EmailNotFoundException;
import com.app.greenFuxes.exception.user.UserManipulationException;
import com.app.greenFuxes.exception.user.UserNotFoundException;
import com.app.greenFuxes.exception.user.UsernameExistException;
import com.app.greenFuxes.repository.UserRepository;
import com.app.greenFuxes.security.LoginAttemptService;
import com.app.greenFuxes.security.Role;
import com.app.greenFuxes.security.UserPrincipal;
import java.io.IOException;
import java.util.List;
import javax.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@Qualifier("UserDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {

  public static final String USERNAME_ALREADY_EXISTS = "Username is already exists";
  public static final String EMAIL_ALREADY_EXISTS = "Email is already exists";
  public static final String USER_NOT_FOUND_BY_USERNAME = "User not found by username: ";
  public static final String FOUND_USER_BY_USERNAME = "Returning found user by username: ";
  public static final String NO_USER_FOUND_BY_EMAIL = "Returning found user by email: ";

  private Logger LOGGER;
  private BCryptPasswordEncoder passwordEncoder;
  private UserRepository userRepository;
  private ModelMapper modelMapper;
  private LoginAttemptService loginAttemptService;

  @Autowired
  public UserServiceImpl(
      BCryptPasswordEncoder passwordEncoder,
      UserRepository userRepository,
      ModelMapper modelMapper,
      LoginAttemptService loginAttemptService,
      Logger LOGGER) {
    this.passwordEncoder = passwordEncoder;
    this.userRepository = userRepository;
    this.modelMapper = modelMapper;
    this.loginAttemptService = loginAttemptService;
    this.LOGGER = LOGGER;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findUserByUserName(username);
    if (user == null) {
      LOGGER.error(USER_NOT_FOUND_BY_USERNAME + username);
      throw new UsernameNotFoundException(USER_NOT_FOUND_BY_USERNAME + username);
    } else {
      validateLoginAttempt(user);
      userRepository.save(user);
      UserPrincipal userPrincipal = new UserPrincipal(user);
      LOGGER.info(FOUND_USER_BY_USERNAME + username);
      return userPrincipal;
    }
  }

  @Override
  public User register(RegistrationDTO registrationDTO) throws UserManipulationException {
    registrationValidation(registrationDTO.getUserName(), registrationDTO.getEmail());
    User user = modelMapper.map(registrationDTO, User.class);
    user.setPassword(encodePassword(registrationDTO.getPassword()));
    user.setActive(false);
    user.setNotLocked(true);
    user.setRole(Role.ROLE_USER.name());
    user.setAuthorities(Role.ROLE_USER.getAuthorities());
    user.setProfileImageUrl("https://robohash.org/" + registrationDTO.getUserName() + ".png");
    userRepository.save(user);
    LOGGER.info("New user created: " + registrationDTO.getUserName());
    return user;
  }

  @Override
  public List<User> getUsers() {
    return userRepository.findAll();
  }

  @Override
  public User findByUsername(String username) throws UserNotFoundException {
    User user = userRepository.findUserByUserName(username);
    if (user == null) {
      throw new UserNotFoundException(USER_NOT_FOUND_BY_USERNAME + username);
    }
    return user;
  }

  @Override
  public User findByEmail(String email) throws EmailNotFoundException {
    User user = userRepository.findUserByEmail(email);
    if (user == null) {
      throw new EmailNotFoundException(NO_USER_FOUND_BY_EMAIL + email);
    }
    return user;
  }

  @Override
  public User addUser(NewUserDTO newUserDTO) throws UserManipulationException, IOException {
    registrationValidation(newUserDTO.getUserName(), newUserDTO.getEmail());
    User user = modelMapper.map(newUserDTO, User.class);
    user.setPassword(encodePassword(newUserDTO.getPassword()));
    user.setProfileImageUrl("https://robohash.org/" + newUserDTO.getUserName() + ".png");
    user.setActive(true);
    user.setNotLocked(true);
    userRepository.save(user);
    return user;
  }

  @Override
  public User findById(Long id) throws UserNotFoundException {
    User user = userRepository.findById(id).orElse(null);
    if (user == null) {
      throw new UserNotFoundException("User not found with the provided id.");
    }
    return user;
  }

  @Override
  public void deleteUser(Long id) throws UserNotFoundException {
    User user = userRepository.findById(id).orElse(null);
    if (user == null) {
      throw new UserNotFoundException(
          "User can't be removed because user does not exist wit the provided id.");
    } else {
      userRepository.deleteById(id);
    }
  }

  @Override
  public void activateUser(Long id) throws UserNotFoundException {
    User user = findById(id);
    user.setActive(true);
    userRepository.save(user);
  }

  @EventListener(ApplicationReadyEvent.class)
  public void createFirstAdmin() {
    createUser("user", encodePassword("user"), Role.ROLE_USER);
    createUser("user2", encodePassword("user2"), Role.ROLE_USER);
    createUser("admin", encodePassword("admin"), Role.ROLE_SUPER_ADMIN);
  }

  public void createUser(String userName, String password, Role role) {
    User user = new User(userName, password, role.name(), role.getAuthorities(), true, true);
    userRepository.save(user);
  }

  @Override
  public String getUserImageUrl(Long id) throws UserNotFoundException {
    User user = userRepository.findById(id).orElse(null);
    if (user != null) {
      return user.getProfileImageUrl();
    } else {
      throw new UserNotFoundException("User does not exist wit the provided id.");
    }
  }

  @Override
  public void saveUser(User user) {
    userRepository.save(user);
  }

  @Override
  public UserProfileDTO userProfile(String username) throws UserNotFoundException {
    User user = findByUsername(username);
    return modelMapper.map(user, UserProfileDTO.class);
  }

  private String encodePassword(String password) {
    return passwordEncoder.encode(password);
  }

  private void validateLoginAttempt(User user) {
    if (user.getNotLocked()) {
      if (loginAttemptService.hasExceededMaxAttempts(user.getUserName())) {
        user.setNotLocked(false);
      } else {
        user.setNotLocked(true);
      }
    } else {
      loginAttemptService.evictUserFromLoginAttemptCache(user.getUserName());
    }
  }

  private void registrationValidation(String username, String email)
      throws UsernameExistException, EmailExistException {
    if (StringUtils.isNoneBlank(username)) {
      User userByUserName = userRepository.findUserByUserName(username);
      User userByEmail = userRepository.findUserByEmail(email);
      if (userByUserName != null) {
        throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
      }
      if (userByEmail != null) {
        throw new EmailExistException(EMAIL_ALREADY_EXISTS);
      }
    }
  }
}
