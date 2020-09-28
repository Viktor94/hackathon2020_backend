package com.app.greenFuxes.service.canteen;

import com.app.greenFuxes.entity.canteen.Canteen;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class CanteenManager {

    private static CanteenManager instance;
    private ArrayList<Canteen> canteenList = new ArrayList<>();

    public static CanteenManager getInstance() {
        if (instance == null) {
            instance = new CanteenManager();
        }
        return instance;
    }

    public void createCanteen(Long officeId) {
        canteenList.add(new Canteen(officeId));
    }

    public Canteen findCanteenByOfficeId(Long officeId) {
      return canteenList.stream().filter(c-> c.getOfficeId().equals(officeId)).findFirst().orElse(null);
    }
}
