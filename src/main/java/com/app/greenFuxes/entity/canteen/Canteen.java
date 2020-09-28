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
    private LinkedBlockingQueue<User> usersInCanteen = new LinkedBlockingQueue<>(this.maxCanteenCapacity);
    private Queue<User> userQueue = new LinkedList<>();
    private Map<User, Date> lunchStarted = new HashMap<>();

    public Canteen(Long officeId) {
        this.officeId = officeId;
    }

    public boolean lunchUser(User user) {
        if (this.usersInCanteen.offer(user)) {
            this.lunchStarted.put(user, new Date(System.currentTimeMillis()));
            return true;
        } else {
            this.userQueue.add(user);
            return false;
        }
    }

    public void finishLunch(User user) {
        this.usersInCanteen.remove(user);
        this.lunchStarted.remove(user);
        User nextUser = this.userQueue.poll();
        if (nextUser != null) {
            this.usersInCanteen.add(nextUser);
            this.lunchStarted.put(user, new Date(System.currentTimeMillis()));
        }
    }

    public void restartDay() {
        this.usersInCanteen = new LinkedBlockingQueue<>(this.maxCanteenCapacity);
        this.userQueue = new LinkedList<>();
        this.lunchStarted = new HashMap<>();
    }

    public void kickGreedy() {
        for (Map.Entry<User, Date> user : this.lunchStarted.entrySet()) {
            if (new Date(System.currentTimeMillis()).getTime() / 100000 - user.getValue().getTime() / 100000 > this.lunchtimeInMinute) {
                finishLunch(user.getKey());
            }
        }
    }
}
