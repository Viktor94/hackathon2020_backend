package com.app.greenFuxes.controller.office;

import com.app.greenFuxes.dto.LED.DurationDTO;
import com.app.greenFuxes.dto.office.CapacityDTO;
import com.app.greenFuxes.service.LED.LEDEnum;
import com.app.greenFuxes.service.LED.LEDService;
import com.app.greenFuxes.service.office.OfficeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/office-status")
@PreAuthorize("hasAnyAuthority('superAdmin')")
public class OfficeStatusController {

  private final OfficeService officeService;
  private final LEDService ledService;

  @Autowired
  public OfficeStatusController(OfficeService officeService, LEDService ledService) {
    this.officeService = officeService;
    this.ledService = ledService;
  }

  @PostMapping("/set-headcount")
  public ResponseEntity<?> setCapacity(@RequestBody CapacityDTO capacityDTO) throws Exception {
    officeService.setHeadCount(capacityDTO.getCapacity());
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping("/led-lock/test")
  public ResponseEntity<?> lockLED(@RequestBody DurationDTO durationDTO) {
    ledService.lock(durationDTO);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping("/led-send/test/{color}")
  public ResponseEntity<?> testLED(@PathVariable String color) throws Exception {
    LEDEnum ledEnum;
    switch (color.toUpperCase()) {
      case "RED":
        ledEnum = LEDEnum.RED;
        break;
      case "GREEN":
        ledEnum = LEDEnum.GREEN;
        break;
      case "ORANGE":
        ledEnum = LEDEnum.ORANGE;
        break;
      default:
        throw new IllegalStateException("Unexpected value: " + color);
    }
    ledService.send(ledEnum);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping("/led-stop/test")
  public ResponseEntity<?> stopTest() {
    ledService.stop();
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
