package com.app.greenFuxes.kafka;

import com.app.greenFuxes.entity.user.User;

public interface KafkaMessageService {

    void notify3rdUserAboutUpcomingVacancyInCanteen(User user);
}