package com.vivektiwary.RoutingAPI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RoutingApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(RoutingApiApplication.class, args);
  }
}
