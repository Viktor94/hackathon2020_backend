package com.app.greenFuxes.service.canteen;

import com.app.greenFuxes.dto.canteen.CanteenSettingDTO;
import com.app.greenFuxes.dto.canteen.CanteenStatusDTO;
import com.app.greenFuxes.entity.canteen.Canteen;
import com.app.greenFuxes.entity.user.User;
import com.app.greenFuxes.kafka.KafkaMessageService;
import com.app.greenFuxes.service.email.EmailSenderService;
import java.util.Date;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CanteenServiceImpl implements CanteenService {

  private final EmailSenderService emailSenderService;

  private final KafkaMessageService kafkaMessageService;

  @Autowired
  public CanteenServiceImpl(
      EmailSenderService emailSenderService, KafkaMessageService kafkaMessageService) {
    this.emailSenderService = emailSenderService;
    this.kafkaMessageService = kafkaMessageService;
  }

  @Override
  public Canteen addCanteen(Long officeId) {
    return CanteenManager.getInstance().createCanteen(officeId);
  }

  @Override
  public Canteen findCanteenByOfficeId(Long officeId) {
    return CanteenManager.getInstance().findCanteenByOfficeId(officeId);
  }

  @Override
  public String lunchUser(User user) {
    if (findCanteenByOfficeId(extractOfficeIdFromUser(user)).lunchUser(user)) {
      return "You can go to the canteen.";
    } else {
      return "You are placed into the queue. You are going to get notification as soon as you can go to the canteen.";
    }
  }

  @Override
  public void finishLunch(User user) {
    Canteen canteen = findCanteenByOfficeId(extractOfficeIdFromUser(user));
    User nextUser = canteen.finishLunch(user);
    kafkaMessageService.notify3rdUserAboutUpcomingVacancyInCanteen(canteen.get3rdUserInUserQueue());
    if (nextUser != null) {
      emailSenderService.sendQueueNotificationEmail(nextUser, canteen.getLunchtimeInMinute());
    }
  }

  private Long extractOfficeIdFromUser(User user) {
    return user.getReservedDate().stream().findFirst().get().getOffice().getId();
  }

  @Override
  @Scheduled(fixedRate = 300000)
  public void kickGreedy() {
    for (Canteen canteen : CanteenManager.getInstance().getCanteenList()) {
      for (Map.Entry<User, Date> user : canteen.getLunchStarted().entrySet()) {
        long timeSpent =
            (new Date(System.currentTimeMillis()).getTime() - user.getValue().getTime()) / 100000;
        if (timeSpent > canteen.getLunchtimeInMinute()) {
          User kickUser = user.getKey();
          User nextUser = canteen.finishLunch(kickUser);
          emailSenderService.sendKickFromCanteenNotificationEmail(kickUser);
          emailSenderService.sendQueueNotificationEmail(nextUser, canteen.getLunchtimeInMinute());
        }
        if (timeSpent >= (canteen.getLunchtimeInMinute() - 5)
            && timeSpent < canteen.getLunchtimeInMinute()) {
          User alarmUser = user.getKey();
          Integer timeLef =
              Math.toIntExact(Integer.valueOf(canteen.getLunchtimeInMinute()) - timeSpent);
          emailSenderService.sendLessThenFiveMinLeftNotificationEmail(alarmUser, timeLef);
        }
      }
    }
  }

  @Override
  public void restartDay(User user) {
    findCanteenByOfficeId(extractOfficeIdFromUser(user)).restartDay();
  }

  @Override
  public void configureCanteen(User user, CanteenSettingDTO canteenSettingDTO) {
    findCanteenByOfficeId(extractOfficeIdFromUser(user))
        .setLunchtimeInMinute(canteenSettingDTO.getLunchtimeInMinute());
    findCanteenByOfficeId(extractOfficeIdFromUser(user))
        .setMaxCanteenCapacity(canteenSettingDTO.getMaxCanteenCapacity());
  }

  @Override
  public CanteenStatusDTO canteenStatus(User user) {
    Canteen canteen = findCanteenByOfficeId(extractOfficeIdFromUser(user));
    return new CanteenStatusDTO(canteen);
  }

  @Scheduled(cron = "0 0 0 * * ?")
  public void setConfiguration() {
    CanteenManager.getInstance().getCanteenList().forEach(Canteen::restartDay);
  }
}
