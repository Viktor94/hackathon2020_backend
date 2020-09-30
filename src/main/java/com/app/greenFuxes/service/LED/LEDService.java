package com.app.greenFuxes.service.LED;

import com.app.greenFuxes.dto.LED.DurationDTO;

public interface LEDService {

  void lock(DurationDTO durationDTO);

  void testLedDisplay(LEDEnum ledEnum) throws Exception;

  void stop();

  void displayStaringStatusOnLedDisplay();

  void showCurrentCanteenStatusOnLedDisplay() throws Exception;

}
