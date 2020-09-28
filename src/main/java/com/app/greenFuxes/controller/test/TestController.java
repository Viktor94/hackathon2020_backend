package com.app.greenFuxes.controller.test;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = {"/test"})
public class TestController {

    @GetMapping("/testUser")
    ResponseEntity<?> testUser(){
        return new ResponseEntity<>("okéUser", HttpStatus.OK);
    }

    @GetMapping("/testAdmin")
    @PreAuthorize("hasAnyAuthority('user:update')")
    ResponseEntity<?> testAdmin(){
        return new ResponseEntity<>("okéAdmin", HttpStatus.OK);
    }
}
