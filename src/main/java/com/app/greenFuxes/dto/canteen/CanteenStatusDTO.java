package com.app.greenFuxes.dto.canteen;

import com.app.greenFuxes.entity.canteen.Canteen;
import com.app.greenFuxes.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CanteenStatusDTO {

    private Integer freeSpace;
    private Integer lunchTimeLengthInMinute;
    private ArrayList<User> usersInCanteen;
    private ArrayList<User> userQueue = new ArrayList<>();

    public CanteenStatusDTO(Canteen canteen) {
        this.freeSpace = canteen.getUsersInCanteen().remainingCapacity();
        this.lunchTimeLengthInMinute = canteen.getLunchtimeInMinute();
        this.usersInCanteen = new ArrayList<>(canteen.getUsersInCanteen());
        this.userQueue = new ArrayList<>(canteen.getUserQueue());
    }
}
