package com.app.greenFuxes.configuration;

import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@EnableScheduling
public class SwaggerConfig {

  @Bean
  public Docket postsApi() {
    return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select().build();
  }

  private ApiInfo apiInfo() {
    return new ApiInfo(
        "Hackathon Backend App",
        "Office life manager application.",
        "1.0",
        "Free to use",
        new Contact("Green Fuxe$", "https://github.com/Viktor94/hackathon2020_backend", ""),
        "Free to use", "https://github.com/Viktor94/hackathon2020_backend",
        Collections.emptyList());
  }
}
