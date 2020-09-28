package com.app.greenFuxes.repository;

import com.app.greenFuxes.entity.reservedDate.ReservedDate;
import com.app.greenFuxes.entity.user.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface ReserveDateRepository extends CrudRepository<ReservedDate, Long> {

  Optional<ReservedDate> findByDate(String date);
}
