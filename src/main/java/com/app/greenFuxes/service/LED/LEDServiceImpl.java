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

  @Override
  public void send(DurationDTO durationDTO) throws Exception {
    try {
      String data = "";
      data = new String(Files.readAllBytes(Paths.get("src/main/resources/config.txt")));

      int temp = lock(durationDTO);
      ConfigDTO configDTO = new ConfigDTO(data);

      if (temp == 200) {
        client
            .post()
            .uri("/send/" + key)
            .body(Mono.just(configDTO), ConfigDTO.class)
            .exchange()
            .block()
            .rawStatusCode();
      }
    } catch (IOException e) {
      System.out.println("File read failed");
    }
  }

  @Override
  public void stop() {
    stopSending();
    unLock();
  }

  private int lock(DurationDTO durationDTO) throws Exception {
    ClientResponse response =
        client
            .post()
            .uri("/lock/" + key)
            .body(Mono.just(durationDTO), DurationDTO.class)
            .exchange()
            .block();

    return response.rawStatusCode();
  }

  private void unLock() {
    client.post().uri("/unlock/" + key);
  }

  private void stopSending() {
    client.post().uri("/stop/" + key);
  }
}
