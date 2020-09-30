package com.app.greenFuxes.service.reserveDate;

import com.app.greenFuxes.dto.reserveDate.BookingsDTO;
import com.app.greenFuxes.entity.reservedDate.ReservedDate;
import com.app.greenFuxes.entity.user.User;
import com.app.greenFuxes.exception.user.UserNotFoundException;

public interface ReserveDateService {

  void save(ReservedDate reservedDate);

  ReservedDate findByDate(String date);

  void addUserToReserveDate(String date, User user);

  void removeUserFromReserveDate(String date, User user) throws UserNotFoundException;

  BookingsDTO getMyBookings(User user);
}
