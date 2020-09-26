package com.example.hackathon_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableScheduling
@EnableSwagger2
public class HackathonBackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(HackathonBackendApplication.class, args);
  }
}
