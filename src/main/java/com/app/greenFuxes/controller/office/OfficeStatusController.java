package com.app.greenFuxes.controller.office;

import com.app.greenFuxes.dto.office.CapacityDTO;
import com.app.greenFuxes.service.office.OfficeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/office-status")
public class OfficeStatusController {

  private final OfficeService officeService;

  @Autowired
  public OfficeStatusController(OfficeService officeService) {
    this.officeService = officeService;
  }

  @PostMapping("/set-headcount")
  public ResponseEntity<?> setCapacity(@RequestBody CapacityDTO capacityDTO) throws Exception {
    officeService.setHeadCount(capacityDTO.getCapacity());
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
