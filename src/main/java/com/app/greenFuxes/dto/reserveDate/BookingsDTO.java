package com.app.greenFuxes.dto.reserveDate;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BookingsDTO {

  private List<String> date = new ArrayList<>();

  public void addDate(String date) {
    this.date.add(date);
  }
}
