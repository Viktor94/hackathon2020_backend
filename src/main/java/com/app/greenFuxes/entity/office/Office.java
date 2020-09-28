package com.app.greenFuxes.entity.office;

import com.app.greenFuxes.entity.reservedDate.ReservedDate;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Office {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private Long capacity;

  @OneToMany(mappedBy = "office")
  private List<ReservedDate> reservedDates;
}
