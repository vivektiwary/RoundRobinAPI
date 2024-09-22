package com.vivektiwary.RoutingAPI.service;

import com.vivektiwary.RoutingAPI.model.ServerInfo;
import com.vivektiwary.RoutingAPI.model.Status;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Service
public class ServerRegistry {

  @Value("${routing-app.gateway.max-request-count}")
  private int maxRequestCount;

  private final Lock lock = new ReentrantLock();
  private final Set<ServerInfo> servers = new LinkedHashSet<>();
  private final RestTemplate restTemplate = new RestTemplate();

  public void registerServer(String serverAddress) {
    System.out.println("Registering server: " + serverAddress);
    servers.add(
        ServerInfo.builder()
            .serverAddress(serverAddress)
            .status(Status.UP)
            .requestCount(new AtomicInteger(0))
            .lastUpdatedAt(System.currentTimeMillis())
            .build());
  }

  public void deregisterServer(String serverAddress) {
    System.out.println("Deregistering server: " + serverAddress);
    ServerInfo serverInfo = ServerInfo.builder().serverAddress(serverAddress).build();
    servers.remove(serverInfo);
  }

  public Set<ServerInfo> getServers() {
    return servers;
  }

  public Set<ServerInfo> getUpServers() {
    return servers.stream()
        .filter(server -> server.getStatus().equals(Status.UP))
        .filter(server -> server.getRequestCount().get() < maxRequestCount)
        .collect(Collectors.toSet());
  }

  public void updateServerHeartbeat(String serverAddress) {
    //    System.out.println("Updating server heartbeat: " + serverAddress);

    lock.lock();
    try {
      servers.stream()
          .filter(server -> server.getServerAddress().equals(serverAddress))
          .findFirst()
          .ifPresent(
              server -> {
                server.setLastUpdatedAt(System.currentTimeMillis());
                server.setStatus(Status.UP);
              });
    } finally {
      lock.unlock();
    }
  }

  @Scheduled(fixedRateString = "${routing-app.gateway.heartbeat-timeout}") // Check every 60 seconds
  public void checkServerHealth() {
    lock.lock();
    try {
      for (ServerInfo server : servers) {
        try {
          String healthCheckUrl = server.getServerAddress() + "/actuator/health";
          Map<String, String> response = restTemplate.getForObject(healthCheckUrl, Map.class);

          System.out.println("Health check response: " + response);
          if (null != response && "UP".equals(response.get("status"))) {
            server.setStatus(Status.UP);
          } else {
            System.out.println("Server is down: " + server.getServerAddress());
            server.setStatus(Status.DOWN);
          }
        } catch (Exception e) {
          System.out.println("Server is down: 123" + server.getServerAddress());
          System.out.println("Exception: " + e.getMessage());
          server.setStatus(Status.DOWN);
        }
        server.setLastUpdatedAt(System.currentTimeMillis());
      }
    } finally {
      lock.unlock();
    }
  }
}
