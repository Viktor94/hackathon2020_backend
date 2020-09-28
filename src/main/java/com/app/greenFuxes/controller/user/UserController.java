package com.app.greenFuxes.controller.user;

import com.app.greenFuxes.dto.http.HttpResponse;
import com.app.greenFuxes.dto.user.NewUserDTO;
import com.app.greenFuxes.dto.user.ProfileImageUpdateDTO;
import com.app.greenFuxes.dto.user.login.LoginDTO;
import com.app.greenFuxes.dto.user.login.LoginResponseDTO;
import com.app.greenFuxes.dto.user.registration.RegistrationDTO;
import com.app.greenFuxes.entity.user.User;
import com.app.greenFuxes.exception.user.UserManipulationException;
import com.app.greenFuxes.exception.user.UserNotFoundException;
import com.app.greenFuxes.security.UserPrincipal;
import com.app.greenFuxes.service.user.UserService;
import com.app.greenFuxes.entity.user.ConfirmationToken;
import com.app.greenFuxes.exception.confirmationtoken.InvalidConfirmationTokenException;
import com.app.greenFuxes.security.JwtUtility;
import com.app.greenFuxes.service.confirmationtoken.ConfirmationTokenService;
import com.app.greenFuxes.service.email.EmailSenderServiceImpl;
import com.app.greenFuxes.util.FileConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping(path = {"/users"})
public class UserController {

    private UserService userService;
    private AuthenticationManager authenticationManager;
    private JwtUtility jwtUtility;
    private ConfirmationTokenService confirmationTokenService;
    private EmailSenderServiceImpl emailSenderServiceImpl;

    @Autowired
    public UserController(UserService userService, AuthenticationManager authenticationManager, JwtUtility jwtUtility, ConfirmationTokenService confirmationTokenService, EmailSenderServiceImpl emailSenderServiceImpl) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtility = jwtUtility;
        this.confirmationTokenService = confirmationTokenService;
        this.emailSenderServiceImpl = emailSenderServiceImpl;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) throws UserNotFoundException {
        authenticate(loginDTO);
        User loginUser = userService.findByUsername(loginDTO.getUserName());
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);
        return new ResponseEntity<>(new LoginResponseDTO("Login was successful!", jwtUtility.generateJwtToken(userPrincipal)), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationDTO userRegDTO) throws UserManipulationException, IOException {
        User newUser = userService.register(userRegDTO);
        ConfirmationToken confTok = confirmationTokenService.saveConfirmationToken(new ConfirmationToken(newUser));
        emailSenderServiceImpl.sendVerificationEmailHTML(newUser,confTok);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/confirm", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<?> confirmUserAccount(@RequestParam("token") String confirmationToken) throws InvalidConfirmationTokenException, UserNotFoundException {
        ConfirmationToken token = confirmationTokenService.findByToken(confirmationToken);
        userService.activateUser(token.getUser().getId());
        confirmationTokenService.deleteConfirmationToken(token.getTokenid());
        return response(HttpStatus.OK, "Activation was successful!");
    }

    @PostMapping("/add")
    public ResponseEntity<User> addNewUser(@RequestBody NewUserDTO newUserDTO) throws IOException, UserManipulationException {
        User newUser = userService.addUser(newUserDTO);
        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findUserById(@PathVariable Long id) throws UserNotFoundException {
        User user = userService.findById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<List<User>> findAllUser() {
        List<User> users = userService.getUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('user:delete')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) throws UserNotFoundException {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/updateProfileImage")
    public ResponseEntity<User> updateProfileImg(@PathVariable Long id, ProfileImageUpdateDTO profImg) throws IOException, UserManipulationException {
        User user = userService.updateProfileImg(id, profImg.getProfileImageUrl());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping(path = "/image/{userName}/{fileName}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getProfileImg(@PathVariable String userName, @PathVariable String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(FileConstant.USER_FOLDER + userName + FileConstant.FORWARD_SLASH + fileName));

    }

    @GetMapping(path = "/image/profile/{userName}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getTempProfileImg(@PathVariable String userName) throws IOException {
        URL url = new URL(FileConstant.TEMP_PROFILE_IMAGE_BASE_URL + userName);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (InputStream inputStream = url.openStream()) {
            int bytesRead;
            byte[] chunk = new byte[1024];
            while ((bytesRead = inputStream.read(chunk)) > 0) {
                byteArrayOutputStream.write(chunk, 0, bytesRead);
            }
        }
        return byteArrayOutputStream.toByteArray();
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String msg) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(), msg.toUpperCase()), httpStatus);
    }

    private void authenticate(LoginDTO loginDTO) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getUserName(), loginDTO.getPassword()));
    }
}
