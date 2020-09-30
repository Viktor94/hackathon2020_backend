package com.app.greenFuxes.service.LED;

import com.app.greenFuxes.dto.LED.ConfigDTO;
import com.app.greenFuxes.dto.LED.DurationDTO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class LEDServiceImpl implements LEDService {

  private final WebClient client =
      WebClient.builder()
          .baseUrl(System.getenv("LED_URL"))
          .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
          .build();
  private final String key = System.getenv("LED_API_KEY");
  private int count = 244;

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
  public void send(LEDEnum ledEnum) throws Exception {
    try {
      String data =
          new String(Files.readAllBytes(Paths.get("src/main/resources/" + ledEnum.getValue())));
      data = data.replace("\"priority\": 0", "\"priority\": " + count);

      ConfigDTO configDTO = new ConfigDTO(data);

      int tempSend =
          client
              .post()
              .uri("/send/" + key)
              .body(Mono.just(configDTO), ConfigDTO.class)
              .exchange()
              .block()
              .rawStatusCode();

      count--;

      System.out.println("/send status code: " + tempSend);
    } catch (IOException e) {
      System.out.println("File read failed");
    }
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
