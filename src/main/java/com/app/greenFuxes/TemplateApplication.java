package com.app.greenFuxes;

import com.app.greenFuxes.util.FileConstant;
import java.io.File;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TemplateApplication {

  public static void main(String[] args) {
    SpringApplication.run(TemplateApplication.class, args);
    new File(FileConstant.USER_FOLDER).mkdirs();
  }
}
