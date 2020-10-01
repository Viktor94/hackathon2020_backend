package com.app.greenFuxes.service.reserveDate;

import com.app.greenFuxes.dto.reserveDate.BookingsDTO;
import com.app.greenFuxes.entity.office.Office;
import com.app.greenFuxes.entity.reservedDate.ReservedDate;
import com.app.greenFuxes.entity.user.User;
import com.app.greenFuxes.exception.user.UserNotFoundException;
import com.app.greenFuxes.repository.ReserveDateRepository;
import com.app.greenFuxes.repository.officeRepository.OfficeRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReserveDateServiceImpl implements ReserveDateService {

  private final ReserveDateRepository reserveDateRepository;
  private final OfficeRepository officeRepository;

  @Autowired
  public ReserveDateServiceImpl(
      ReserveDateRepository reserveDateRepository, OfficeRepository officeRepository) {
    this.reserveDateRepository = reserveDateRepository;
    this.officeRepository = officeRepository;
  }

  @Override
  public void save(ReservedDate reservedDate) {
    reserveDateRepository.save(reservedDate);
  }

  @Override
  public ReservedDate findByDate(String date) {
    return reserveDateRepository.findByDate(date).orElse(null);
  }

  @Override
  public void addUserToReserveDate(String date, User user) {
    ReservedDate reserveDateAlreadyExist = findByDate(date);

    Office office = officeRepository.findById(1L).get();
    if (reserveDateAlreadyExist == null) {
      ReservedDate newReserveDate = new ReservedDate(date);
      office.add(newReserveDate);
      officeRepository.save(office);
      newReserveDate.setOffice(office);
      newReserveDate.addUserToList(user);
      save(newReserveDate);
    } else if (reserveDateAlreadyExist.getUsersInOffice().size() < office.getCapacity()
        && !reserveDateAlreadyExist.getUsersInOffice().contains(user)) {
      reserveDateAlreadyExist.addUserToList(user);
      save(reserveDateAlreadyExist);
    }
  }

  @Override
  public void removeUserFromReserveDate(String date, User user) throws UserNotFoundException {
    ReservedDate reservedDate = findByDate(date);

    if (reservedDate != null && reservedDate.getUsersInOffice().contains(user)) {
      List<User> userList = reservedDate.getUsersInOffice();
      userList.remove(user);
      reservedDate.setUsersInOffice(userList);
      save(reservedDate);
    } else {
      throw new UserNotFoundException("ReserveDate not contains user");
    }
  }

  public BookingsDTO getMyBookings(User user) {
    BookingsDTO bookingsDTO = new BookingsDTO();
    for (int i = 0; i < user.getReservedDate().size(); i++) {
      bookingsDTO.addDate(user.getReservedDate().get(i).getDate());
    }
    return bookingsDTO;
  }
}
