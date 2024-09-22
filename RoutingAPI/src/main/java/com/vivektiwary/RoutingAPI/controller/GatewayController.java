package com.vivektiwary.RoutingAPI.controller;

import com.vivektiwary.RoutingAPI.model.ServerInfo;
import com.vivektiwary.RoutingAPI.model.Status;
import com.vivektiwary.RoutingAPI.service.RoundRobinRouter;
import com.vivektiwary.RoutingAPI.service.ServerRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
public class GatewayController {

  private final RoundRobinRouter roundRobinRouter;
  private final RestTemplate restTemplate;
  private final ServerRegistry serverRegistry;

  @Autowired
  public GatewayController(RoundRobinRouter roundRobinRouter, ServerRegistry serverRegistry) {
    this.roundRobinRouter = roundRobinRouter;
    this.restTemplate = new RestTemplate();
    this.serverRegistry = serverRegistry;
  }

  @PostMapping()
  public Map<String, Object> echoItem(@RequestBody Map<String, Object> item) {
    try {
      ServerInfo nextServer = roundRobinRouter.getNextServer();
      String serverAddress = nextServer.getServerAddress();

      nextServer.incrementRequestCount();
      System.out.println("Current request count: " + nextServer.getRequestCount());
      Map<String, Object> response = restTemplate.postForObject(serverAddress, item, Map.class);
      nextServer.decrementRequestCount();

      return response;
    } catch (Exception e) {
      return Map.of("error", e.getMessage());
    }
  }

  @PostMapping("/register")
  public String registerServer(@RequestBody String serverAddress) {
    serverRegistry.registerServer(serverAddress);
    return "Server registered at: " + serverAddress;
  }

  @PostMapping("/deregister")
  public String deregisterServer(@RequestBody String serverAddress) {
    serverRegistry.deregisterServer(serverAddress);
    return "Server deregistered at: " + serverAddress;
  }

  @PostMapping("/heartbeat")
  public String heartbeat(@RequestBody String serverAddress) {
    serverRegistry.updateServerHeartbeat(serverAddress);
    return "Heartbeat received from: " + serverAddress;
  }
}
