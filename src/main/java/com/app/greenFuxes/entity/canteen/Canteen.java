package com.app.greenFuxes.entity.canteen;

import com.app.greenFuxes.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Canteen {

  private Long officeId;
  private Integer maxCanteenCapacity = 10;
  private Integer lunchtimeInMinute = 30;
  private LinkedBlockingQueue<User> usersInCanteen =
      new LinkedBlockingQueue<>(this.maxCanteenCapacity);
  private Queue<User> userQueue = new LinkedList<>();
  private Map<User, Date> lunchStarted = new HashMap<>();

  public Canteen(Long officeId) {
    this.officeId = officeId;
  }

  public boolean lunchUser(User user) {
    if (usersInCanteen.contains(user)) {
      return true;
    }
    if (userQueue.contains(user)) {
      return false;
    }

    if (this.usersInCanteen.offer(user)) {
      this.lunchStarted.put(user, new Date(System.currentTimeMillis()));
      return true;
    } else {
      this.userQueue.add(user);
      return false;
    }
  }

  public User finishLunch(User user) {
    this.usersInCanteen.remove(user);
    this.lunchStarted.remove(user);
    User nextUser = this.userQueue.poll();
    if (nextUser != null) {
      this.usersInCanteen.add(nextUser);
      this.lunchStarted.put(nextUser, new Date(System.currentTimeMillis()));
      return nextUser;
    } else {
      return null;
    }
  }

  public void restartDay() {
    this.usersInCanteen = new LinkedBlockingQueue<>(this.maxCanteenCapacity);
    this.userQueue = new LinkedList<>();
    this.lunchStarted = new HashMap<>();
  }

  public User get3rdUserInUserQueue() {
    if (userQueue.size() == 0) {
      return null;
    }
    if (userQueue.size() <= 3) {
      ArrayList<User> listOfUsersInQueue = new ArrayList(userQueue);
      return listOfUsersInQueue.get(2);
    }
    return null;
  }
}
