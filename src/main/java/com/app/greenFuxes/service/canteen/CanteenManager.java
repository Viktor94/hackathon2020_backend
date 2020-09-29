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

    public Canteen createCanteen(Long officeId) {
        Canteen canteen = new Canteen(officeId);
        canteenList.add(canteen);
        return canteen;
    }

    public Canteen findCanteenByOfficeId(Long officeId) {
        Canteen canteen = canteenList.stream().filter(c-> c.getOfficeId().equals(officeId)).findFirst().orElse(null);
        if (canteen==null){
            return createCanteen(officeId);
        }else {
            return canteen;
        }

    }
}
