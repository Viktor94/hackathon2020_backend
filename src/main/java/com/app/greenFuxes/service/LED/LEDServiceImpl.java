package com.app.greenFuxes.service.LED;

import com.app.greenFuxes.dto.LED.ConfigDTO;
import com.app.greenFuxes.dto.LED.DurationDTO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.app.greenFuxes.service.canteen.CanteenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class LEDServiceImpl implements LEDService {

  private final CanteenService canteenService;

  private final WebClient client =
      WebClient.builder()
          .baseUrl(System.getenv("LED_URL"))
          .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
          .build();
  private final String key = System.getenv("LED_API_KEY");

  @Autowired
  public LEDServiceImpl(CanteenService canteenService) {
    this.canteenService = canteenService;
  }

  @Override
  public void lock(DurationDTO durationDTO) {
    int response =
        client
            .post()
            .uri("/lock/" + key)
            .body(Mono.just(durationDTO), DurationDTO.class)
            .exchange()
            .block()
            .rawStatusCode();
    System.out.println("/lock status code: " + response);
  }

  @Override
  public void testLedDisplay(LEDEnum ledEnum){
    String data = getDataFromLedConfigFile(ledEnum);
    sendDataToLedDisplay(data, canteenService.getLedPriorityCounter() - 1);
  }

  private String getDataFromLedConfigFile(LEDEnum ledEnum) {
    String ledConfig = null;
    try {
      ledConfig = new String(Files.readAllBytes(Paths.get("src/main/resources/" + ledEnum.getValue())));
    } catch (IOException e) {
      System.out.println("File read failed");
    }
    return ledConfig;
  }

  private void sendDataToLedDisplay(String ledConfig, int ledPriorityCounter) {
    ledConfig = ledConfig.replace("\"priority\": 0", "\"priority\": " + ledPriorityCounter);
    ConfigDTO configDTO = new ConfigDTO(ledConfig);
    int responseCode = postDataToLedAPIAndReturnResponseCode(configDTO);
    System.out.println("/send status code: " + responseCode);
  }

  private int postDataToLedAPIAndReturnResponseCode(ConfigDTO configDTO) {
    return client
        .post()
        .uri("/send/" + key)
        .body(Mono.just(configDTO), ConfigDTO.class)
        .exchange()
        .block()
        .rawStatusCode();
  }

  @Override
  @Scheduled(cron = "0 1 1 * * ?")
  public void displayStaringStatusOnLedDisplay() {
    LEDEnum ledEnum = LEDEnum.GREEN;
    canteenService.setCanteenLedStatus(ledEnum);
    canteenService.setLedPriorityCounter(255);
    String data = getDataFromLedConfigFile(ledEnum);
    sendDataToLedDisplay(data, 255);
  }

  @Override
  @Scheduled(fixedRate = 30000)
  public void showCurrentCanteenStatusOnLedDisplay(){
    LEDEnum currentStatus = calculateCurrentStatus();
    LEDEnum changedStatus =
            checkIfStatusHasChangedAndReturnNewStatus(canteenService.getCanteenLedStatus(),currentStatus);
    if (changedStatus != null) {
      canteenService.setCanteenLedStatus(changedStatus);
      int increasedPriority = canteenService.getLedPriorityCounter() - 10;
      String ledConfigData = getDataFromLedConfigFile(changedStatus);
      sendDataToLedDisplay(ledConfigData, increasedPriority);
      canteenService.setLedPriorityCounter(increasedPriority);
    }
  }

  private LEDEnum calculateCurrentStatus() {
    int freeSpace = canteenService.getFreeSpaceInCanteen();
    int canteenCapacity = canteenService.getCanteenCapacity();
    LEDEnum currentStatus;
    if ( 0.2f * canteenCapacity < freeSpace) {
      currentStatus = LEDEnum.GREEN;
      //System.out.println("Status is GREEN");
    } else if (0 < freeSpace && freeSpace <= 0.2f * canteenCapacity) {
      //System.out.println("Status is ORANGE");
      currentStatus = LEDEnum.ORANGE;
    } else {
      currentStatus = LEDEnum.RED;
    }
    return currentStatus;
  }

  private LEDEnum checkIfStatusHasChangedAndReturnNewStatus(
      LEDEnum previousStatus, LEDEnum currentStatus) {
    if (previousStatus == currentStatus) {
      return null;
    }
    return currentStatus;
  }

  @Override
  public void stop() {
    stopSending();
    unLock();
  }

  private void unLock() {
    int tempUnLock = client.post().uri("/unlock/" + key).exchange().block().rawStatusCode();
    System.out.println("/unlock status code: " + tempUnLock);
  }

  private void stopSending() {
    int tempStop = client.post().uri("/stop/" + key).exchange().block().rawStatusCode();
    System.out.println("/stop status code: " + tempStop);
  }
}
