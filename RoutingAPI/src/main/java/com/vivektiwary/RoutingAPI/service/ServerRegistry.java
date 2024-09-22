package com.vivektiwary.RoutingAPI.service;

import com.vivektiwary.RoutingAPI.model.ServerInfo;
import com.vivektiwary.RoutingAPI.model.Status;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ServerRegistry {

  @Value("${routing-app.gateway.max-request-count}")
  private int maxRequestCount;

  private final Set<ServerInfo> servers = new HashSet<>();
  private final RestTemplate restTemplate = new RestTemplate();

  public void registerServer(String serverAddress) {
    System.out.println("Registering server: " + serverAddress);
    servers.add(
        ServerInfo.builder()
            .serverAddress(serverAddress)
            .status(Status.UP)
            .lastUpdatedAt(System.currentTimeMillis())
            .build());
  }

  public void deregisterServer(String serverAddress) {
    System.out.println("Deregistering server: " + serverAddress);
    ServerInfo serverInfo = ServerInfo.builder().serverAddress(serverAddress).build();
    servers.remove(serverInfo);
  }

  public Set<ServerInfo> getUpServers() {
    return servers.stream()
        .filter(server -> server.getStatus().equals(Status.UP))
        .filter(server -> server.getRequestCount().get() < maxRequestCount)
        .collect(Collectors.toSet());
  }

  public void updateServerHeartbeat(String serverAddress) {
    System.out.println("Updating server heartbeat: " + serverAddress);

    servers.stream()
        .filter(server -> server.getServerAddress().equals(serverAddress))
        .findFirst()
        .ifPresent(
            server -> {
              server.setLastUpdatedAt(System.currentTimeMillis());
              server.setStatus(Status.UP);
            });
  }

  @Scheduled(fixedRateString = "${routing-app.gateway.heartbeat-timeout}") // Check every 60 seconds
  public void checkServerHealth() {
    for (ServerInfo server : servers) {
      try {
        String healthCheckUrl = server.getServerAddress() + "/health";
        String response = restTemplate.getForObject(healthCheckUrl, String.class);

        if ("OK".equals(response)) {
          server.setStatus(Status.UP);
        } else {
          System.out.println("Server is down: " + server.getServerAddress());
          server.setStatus(Status.DOWN);
        }
      } catch (Exception e) {
        server.setStatus(Status.DOWN);
      }
      server.setLastUpdatedAt(System.currentTimeMillis());
    }
  }
}
