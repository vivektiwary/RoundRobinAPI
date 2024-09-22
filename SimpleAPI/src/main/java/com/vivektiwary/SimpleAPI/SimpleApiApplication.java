package com.vivektiwary.SimpleAPI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SimpleApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(SimpleApiApplication.class, args);
  }
}
