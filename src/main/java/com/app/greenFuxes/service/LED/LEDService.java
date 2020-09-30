package com.app.greenFuxes.service.LED;

import com.app.greenFuxes.dto.LED.DurationDTO;

public interface LEDService {

  void send(DurationDTO durationDTO, LEDEnum ledEnum) throws Exception;

  void stop();
}