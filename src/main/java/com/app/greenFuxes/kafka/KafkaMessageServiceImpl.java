package com.app.greenFuxes.kafka;


import com.app.greenFuxes.entity.user.User;
import com.app.greenFuxes.service.canteen.CanteenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaMessageServiceImpl implements KafkaMessageService {

  @Autowired
  private KafkaTemplate<String, KafkaMessage> kafkaTemplate;

  @Autowired
  public KafkaMessageServiceImpl() {
    ;
  }

  @Override
  public void notify3rdUserAboutUpcomingVacancyInCanteen(User user) {
    if (user != null) {
      String messageType = KafkaMessageType.NOTIFICATION_OF_ENTRY.getValue();
      String messageText = "Dear " + user.getFirstName() + " (" + user.getLastName() + " )Your place "
                           + "in the queue is: 3."
                           + "You will be allowed to enter the canteen soon.";
      kafkaTemplate.send(messageType, new KafkaMessage(messageType, messageText));
    }
  }
}

