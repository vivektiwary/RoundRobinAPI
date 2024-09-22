package com.vivektiwary.SimpleAPI.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HeartbeatSender {
  private final RestTemplate restTemplate = new RestTemplate();

  @Value("${routing-app.gateway.url}")
  private String gatewayUrl;

  @Value("${routing-app.gateway.heartbeat}")
  private String heartbeatPath;

  @Value("${server.port}")
  private String serverPort;

  @Value("${server.host}")
  private String serverHost;

  @Scheduled(fixedRateString = "${routing-app.gateway.heartbeat-interval}")
  public void sendHeartbeat() {
    String serverAddress = serverHost + ":" + serverPort;

    System.out.println("Sending heartbeat..." + gatewayUrl + heartbeatPath);
    try {
      restTemplate.postForObject(gatewayUrl + heartbeatPath, serverAddress, String.class);
      System.out.println("Heartbeat sent successfully");
    } catch (Exception e) {
      System.err.println("Failed to send heartbeat: " + e.getMessage());
    }
  }
}
