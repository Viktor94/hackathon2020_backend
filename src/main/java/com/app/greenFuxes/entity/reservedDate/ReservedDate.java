package com.app.greenFuxes.entity.reservedDate;

import com.app.greenFuxes.entity.office.Office;
import com.app.greenFuxes.entity.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class ReservedDate {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String date;

  @ManyToMany(cascade = {CascadeType.ALL})
  private List<User> usersInOffice = new ArrayList<>();

  @ManyToOne @JsonBackReference private Office office;

  public ReservedDate() {
    this.date = convertDateToSimpleDate();
  }

  public ReservedDate(String date) {
    this.date = date;
  }

  public void addUserToList(User user) {
    this.usersInOffice.add(user);
  }

  public void removeUserFromList(User user) {
    this.usersInOffice.remove(user);
  }

  public String convertDateToSimpleDate() {
    Date date1 = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    return dateFormat.format(date1);
  }
}
