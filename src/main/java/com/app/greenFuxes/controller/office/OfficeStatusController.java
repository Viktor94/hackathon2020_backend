package com.app.greenFuxes.controller.office;

import com.app.greenFuxes.dto.LED.DurationDTO;
import com.app.greenFuxes.dto.office.CapacityDTO;
import com.app.greenFuxes.service.LED.LEDService;
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
  private final LEDService ledService;

  @Autowired
  public OfficeStatusController(OfficeService officeService,
      LEDService ledService) {
    this.officeService = officeService;
    this.ledService = ledService;
  }

  @PostMapping("/set-headcount")
  public ResponseEntity<?> setCapacity(@RequestBody CapacityDTO capacityDTO) throws Exception {
    officeService.setHeadCount(capacityDTO.getCapacity());
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping("/led-send/test")
  public ResponseEntity<?> testLED(@RequestBody DurationDTO durationDTO)
      throws Exception {
    ledService.send(durationDTO);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping("/led-stop/test")
  public ResponseEntity<?> stopTest() {
    ledService.stop();
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
