package com.app.greenFuxes.controller.office;

import com.app.greenFuxes.service.canteen.CanteenService;
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
  private CanteenService canteenService;

  @Autowired
  public OfficeController(OfficeService officeService, CanteenService canteenService) {
    this.officeService = officeService;
    this.canteenService = canteenService;
  }

  @PostMapping("/create")
  public ResponseEntity<?> createOffice() throws Exception {
    canteenService.addCanteen(officeService.create().getId());
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
