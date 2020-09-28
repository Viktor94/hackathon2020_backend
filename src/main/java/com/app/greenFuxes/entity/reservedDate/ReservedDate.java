package com.app.greenFuxes.entity.reservedDate;

import com.app.greenFuxes.entity.office.Office;
import com.app.greenFuxes.entity.user.User;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ReservedDate {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private Date date;

  @ManyToMany
  private List<User> usersInOffice;

  @ManyToOne
  private Office office;
}
