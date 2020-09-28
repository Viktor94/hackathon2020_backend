package com.app.greenFuxes.entity.office;

import com.app.greenFuxes.entity.reservedDate.ReservedDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Office {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private int capacity;

  @OneToMany(mappedBy = "office")
  private List<ReservedDate> reservedDates = new ArrayList<>();

  public void add(ReservedDate reservedDate) {
    this.reservedDates.add(reservedDate);
  }
}
