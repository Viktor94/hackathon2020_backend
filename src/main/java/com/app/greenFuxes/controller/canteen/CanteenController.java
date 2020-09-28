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
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

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
    ResponseEntity<?> bookCanteenPlace(@RequestBody Principal userPrincipal) throws UserNotFoundException {
        return response(HttpStatus.OK, canteenService.lunchUser(findUserByPrincipal(userPrincipal)));
    }

    @PostMapping("/finish")
    ResponseEntity<?> finishLunch(@RequestBody Principal userPrincipal) throws UserNotFoundException {
        canteenService.finishLunch(findUserByPrincipal(userPrincipal));
        return response(HttpStatus.OK, "Finishing lunch was successful!");
    }

    @GetMapping("/status")
    ResponseEntity<?> getCanteenStatus(@RequestBody Principal userPrincipal) throws UserNotFoundException {
        return new ResponseEntity<>(canteenService.canteenStatus(findUserByPrincipal(userPrincipal)), HttpStatus.OK);
    }

    @PutMapping("/configure")
    ResponseEntity<?> configureCanteen(@RequestBody CanteenSettingDTO canteenSettingDTO, Principal userPrincipal) throws UserNotFoundException {
        canteenService.configureCanteen(findUserByPrincipal(userPrincipal), canteenSettingDTO);
        return response(HttpStatus.OK, "Configuration was successful!");
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String msg) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(), msg.toUpperCase()), httpStatus);
    }

    private User findUserByPrincipal(Principal userPrincipal) throws UserNotFoundException {
        return userService.findByUsername(userPrincipal.getName());
    }
}
