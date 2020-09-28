package com.app.greenFuxes.service.canteen;

import com.app.greenFuxes.dto.canteen.CanteenSettingDTO;
import com.app.greenFuxes.dto.canteen.CanteenStatusDTO;
import com.app.greenFuxes.entity.canteen.Canteen;
import com.app.greenFuxes.entity.user.User;

public interface CanteenService {
    void addCanteen(Long officeId);

    Canteen findCanteenByOfficeId(Long officeId);

    String lunchUser(User user);

    void finishLunch(User user);

    void kickGreedy();

    void restartDay(User user);

    void configureCanteen(User user, CanteenSettingDTO canteenSettingDTO);

    CanteenStatusDTO canteenStatus(User user);
}
