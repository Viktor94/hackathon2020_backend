package com.app.greenFuxes.service.reserveDate;

import com.app.greenFuxes.entity.reservedDate.ReservedDate;
import com.app.greenFuxes.entity.user.User;
import java.text.SimpleDateFormat;
import java.util.Optional;

public interface ReserveDateService {

  void save(ReservedDate reservedDate);

  ReservedDate findByDate(String date);

  void addUserToReserveDate(String date, User user);
}
