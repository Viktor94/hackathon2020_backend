package com.app.greenFuxes.controller.office;

import com.app.greenFuxes.dto.DateDTO;
import com.app.greenFuxes.dto.office.UsersInOfficeDTO;
import com.app.greenFuxes.entity.office.Office;
import com.app.greenFuxes.entity.user.User;
import com.app.greenFuxes.service.canteen.CanteenService;
import com.app.greenFuxes.service.office.OfficeService;
import com.app.greenFuxes.service.reserveDate.ReserveDateService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/office")
public class OfficeController {

  private final OfficeService officeService;
  private final CanteenService canteenService;
  private final ReserveDateService reserveDateService;

  @Autowired
  public OfficeController(
      OfficeService officeService,
      CanteenService canteenService,
      ReserveDateService reserveDateService) {
    this.officeService = officeService;
    this.canteenService = canteenService;
    this.reserveDateService = reserveDateService;
  }

  @PostMapping("/create")
  public ResponseEntity<?> createOffice() throws Exception {
    canteenService.addCanteen(officeService.create().getId());
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping("/check-date")
  public ResponseEntity<?> checkDate(@RequestBody DateDTO dateDTO) {
    List<User> list = reserveDateService.findByDate(dateDTO.getDate()).getUsersInOffice();
    Office office = officeService.findById(1L);
    UsersInOfficeDTO usersInOfficeDTO =
        new UsersInOfficeDTO(list, office.getCapacity() - list.size(), office.getCapacity());
    return new ResponseEntity<>(usersInOfficeDTO, HttpStatus.OK);
  }
}
