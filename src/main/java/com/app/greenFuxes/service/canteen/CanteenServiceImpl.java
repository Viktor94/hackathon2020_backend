package com.app.greenFuxes.service.canteen;

import com.app.greenFuxes.dto.canteen.CanteenSettingDTO;
import com.app.greenFuxes.entity.canteen.Canteen;
import com.app.greenFuxes.entity.user.User;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CanteenServiceImpl implements CanteenService {

    @Override
    public void addCanteen(Long officeId) {
        CanteenManager.getInstance().createCanteen(officeId);
    }

    @Override
    public Canteen findCanteenByOfficeId(Long officeId) {
        return CanteenManager.getInstance().findCanteenByOfficeId(officeId);
    }

    @Override
    public String lunchUser(User user) {
        if (findCanteenByOfficeId(extractOfficeIdFromUser(user)).lunchUser(user)){
            return "You can go to the canteen.";
        } else {
            return "You are placed into the queue. You are going to get notification as soon as you can go to the canteen.";
        }

    }

    @Override
    public void finishLunch(User user) {
        findCanteenByOfficeId(extractOfficeIdFromUser(user)).finishLunch(user);
    }

    @Override
    @Scheduled(fixedRate = 60000)
    public void kickGreedy() {
        System.out.println("Egyél kutyyaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        CanteenManager.getInstance().getCanteenList().forEach(Canteen::kickGreedy);
    }

    @Override
    public void restartDay(User user) {
        findCanteenByOfficeId(extractOfficeIdFromUser(user)).restartDay();
    }

    @Override
    public void configureCanteen(User user, CanteenSettingDTO canteenSettingDTO) {
        findCanteenByOfficeId(extractOfficeIdFromUser(user)).setLunchtimeInMinute(canteenSettingDTO.getLunchtimeInMinute());
        findCanteenByOfficeId(extractOfficeIdFromUser(user)).setMaxCanteenCapacity(canteenSettingDTO.getMaxCanteenCapacity());
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void setConfiguration(){
        CanteenManager.getInstance().getCanteenList().forEach(Canteen::restartDay);
    }

    private Long extractOfficeIdFromUser(User user) {
        return user.getReservedDate().getOffice().getId();
    }
}
