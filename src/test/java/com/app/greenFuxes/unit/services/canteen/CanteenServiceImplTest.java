package com.app.greenFuxes.unit.services.canteen;

import com.app.greenFuxes.dto.canteen.CanteenSettingDTO;
import com.app.greenFuxes.dto.canteen.CanteenStatusDTO;
import com.app.greenFuxes.entity.canteen.Canteen;
import com.app.greenFuxes.entity.office.Office;
import com.app.greenFuxes.entity.reservedDate.ReservedDate;
import com.app.greenFuxes.entity.user.User;
import com.app.greenFuxes.kafka.KafkaMessageService;
import com.app.greenFuxes.security.Role;
import com.app.greenFuxes.service.canteen.CanteenManager;
import com.app.greenFuxes.service.canteen.CanteenServiceImpl;
import com.app.greenFuxes.service.email.EmailSenderService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

@RunWith(SpringRunner.class)
public class CanteenServiceImplTest {

  @Mock private EmailSenderService emailSenderService;

  @Mock private KafkaMessageService kafkaMessageService;

  private CanteenServiceImpl canteenService;
  private Canteen testCanteen;
  private ReservedDate reservedDate;
  private Office testOffice;
  private User user;

  @Before
  public void setUp() throws Exception {
    canteenService = new CanteenServiceImpl(emailSenderService, kafkaMessageService);
    CanteenManager.getInstance().setCanteenList(new ArrayList<>());
    Long officeId = 20L;
    testCanteen = canteenService.addCanteen(officeId);

    testOffice = new Office();
    testOffice.setId(officeId);
    reservedDate = new ReservedDate();
    user =
        new User("bob", "bob", Role.ROLE_USER.name(), Role.ROLE_USER.getAuthorities(), true, true);
    reservedDate.setUsersInOffice(new ArrayList<>(Collections.singletonList(user)));
    user.setReservedDate(new ArrayList<>(Collections.singletonList(reservedDate)));
    testOffice.setReservedDates(new ArrayList<>(Collections.singletonList(reservedDate)));
    reservedDate.setOffice(testOffice);
  }

  @Test
  public void assertThatEquals_addCanteen() {
    Canteen canteen1 = canteenService.addCanteen(20L);
    Assert.assertEquals(Long.valueOf(20), canteen1.getOfficeId());
  }

  @Test
  public void assertThatEquals_findCanteenByOfficeId_byExistingOfficeId() {
    Canteen canteen1 = canteenService.findCanteenByOfficeId(20L);
    Assert.assertNotNull(canteen1);
    Assert.assertEquals(Long.valueOf(20), canteen1.getOfficeId());
  }

  @Test
  public void assertThatEquals_findCanteenByOfficeId_byNotExistingOfficeId() {
    Canteen canteen1 = canteenService.findCanteenByOfficeId(10L);
    Assert.assertNotNull(canteen1);
    Assert.assertEquals(Long.valueOf(10), canteen1.getOfficeId());
    Assert.assertEquals(2, CanteenManager.getInstance().getCanteenList().size());
  }

  @Test
  public void assertThatEquals_lunchUser() {
    Assert.assertEquals("You can go to the canteen.", canteenService.lunchUser(user));
    Assert.assertEquals(1, canteenService.findCanteenByOfficeId(20L).getUsersInCanteen().size());
  }

  @Test
  public void assertThatEquals_lunchUser_withNoPlaceInCanteen() {
    canteenService.configureCanteen(user, new CanteenSettingDTO(1, 30));
    canteenService.restartDay(user);
    User otherUser = new User();
    reservedDate.getUsersInOffice().add(otherUser);
    otherUser.setReservedDate(new ArrayList<>(Collections.singletonList(reservedDate)));
    Assert.assertEquals("You can go to the canteen.", canteenService.lunchUser(user));
    Assert.assertEquals(
        "You are placed into the queue. You are going to get notification as soon as you can go to the canteen.",
        canteenService.lunchUser(otherUser));
  }

  @Test
  public void assertThatEquals_configureCanteen_withResetDay() {
    canteenService.configureCanteen(user, new CanteenSettingDTO(2, 30));
    canteenService.restartDay(user);
    CanteenStatusDTO canteenStatusDTO = canteenService.canteenStatus(user);
    Assert.assertEquals(Integer.valueOf(2), canteenStatusDTO.getFreeSpace());
    Assert.assertEquals(Integer.valueOf(30), canteenStatusDTO.getLunchTimeLengthInMinute());
  }

  @Test
  public void assertThatEquals_configureCanteen_withoutResetDay() {
    canteenService.configureCanteen(user, new CanteenSettingDTO(2, 30));
    CanteenStatusDTO canteenStatusDTO = canteenService.canteenStatus(user);
    Assert.assertEquals(Integer.valueOf(10), canteenStatusDTO.getFreeSpace());
    Assert.assertEquals(Integer.valueOf(30), canteenStatusDTO.getLunchTimeLengthInMinute());
  }

  @Test
  public void assertThatEquals_canteenStatus() {
    CanteenStatusDTO canteenStatusDTO = canteenService.canteenStatus(user);
    Assert.assertEquals(Integer.valueOf(10), canteenStatusDTO.getFreeSpace());
    Assert.assertEquals(Integer.valueOf(30), canteenStatusDTO.getLunchTimeLengthInMinute());
  }

  @Test
  public void assertThatEquals_kickGreedy_withNotGreedyUser() {
    Mockito.doNothing().when(emailSenderService).sendQueueNotificationEmail(user, testCanteen.getLunchtimeInMinute());
    canteenService.lunchUser(user);
    Assert.assertEquals(1, canteenService.findCanteenByOfficeId(20L).getUsersInCanteen().size());
    canteenService.kickGreedy();
    Assert.assertEquals(1, canteenService.findCanteenByOfficeId(20L).getUsersInCanteen().size());
  }

  @Test
  public void assertThatEquals_finishLunch() {
    Mockito.doNothing().when(emailSenderService).sendQueueNotificationEmail(user, testCanteen.getLunchtimeInMinute());
    canteenService.lunchUser(user);
    Assert.assertEquals(1, canteenService.findCanteenByOfficeId(20L).getUsersInCanteen().size());
    canteenService.finishLunch(user);
    Assert.assertEquals(0, canteenService.findCanteenByOfficeId(20L).getUsersInCanteen().size());
  }

  @Test
  public void assertThatEquals_kickGreedy_withGreedyUser() {
    Mockito.doNothing().when(emailSenderService).sendQueueNotificationEmail(user, testCanteen.getLunchtimeInMinute());
    canteenService.lunchUser(user);
    Assert.assertEquals(1, canteenService.findCanteenByOfficeId(20L).getUsersInCanteen().size());
    Date date = canteenService.findCanteenByOfficeId(20L).getLunchStarted().get(user);
    Date dateBefore = (new Date(date.getTime() - 4000000));
    canteenService.findCanteenByOfficeId(20L).getLunchStarted().replace(user, date, dateBefore);
    canteenService.kickGreedy();
    Assert.assertEquals(0, canteenService.findCanteenByOfficeId(20L).getUsersInCanteen().size());
  }

  @Test
  public void assertThatEquals_configureCanteen_withMidnightPass() {
    canteenService.configureCanteen(user, new CanteenSettingDTO(2, 30));
    canteenService.setConfiguration();
    CanteenStatusDTO canteenStatusDTO = canteenService.canteenStatus(user);
    Assert.assertEquals(Integer.valueOf(2), canteenStatusDTO.getFreeSpace());
    Assert.assertEquals(Integer.valueOf(30), canteenStatusDTO.getLunchTimeLengthInMinute());
  }
}
