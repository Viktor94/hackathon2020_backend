package com.app.greenFuxes.controller.office;

import com.app.greenFuxes.service.office.OfficeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/office")
public class OfficeController {

  private OfficeService officeService;

  @Autowired
  public OfficeController(OfficeService officeService) {
    this.officeService = officeService;
  }

  @PostMapping("/create")
  public ResponseEntity<?> createOffice() throws Exception {
    officeService.create();
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
