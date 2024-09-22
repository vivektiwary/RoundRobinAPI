package com.vivektiwary.SimpleAPI.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.util.Random;

@RestController
public class SimpleController {

  private final Random random = new Random();

  @PostMapping()
  public Map<String, Object> echoItem(@RequestBody Map<String, Object> item) {
    System.out.println("Received request: ");
//    introduceRandomDelay();
    return item;
  }

  private void introduceRandomDelay() {
    try {
      int delay = random.nextInt(2000); // Random delay between 0 and 5000 milliseconds (5 seconds)
      Thread.sleep(delay);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
