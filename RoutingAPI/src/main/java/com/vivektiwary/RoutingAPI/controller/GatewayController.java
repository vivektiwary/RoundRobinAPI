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

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RestController
public class GatewayController {

  private final RoundRobinRouter roundRobinRouter;
  private final RestTemplate restTemplate;
  private final ServerRegistry serverRegistry;

  private final Lock lock = new ReentrantLock();

  @Autowired
  public GatewayController(RoundRobinRouter roundRobinRouter, ServerRegistry serverRegistry) {
    this.roundRobinRouter = roundRobinRouter;
    this.restTemplate = new RestTemplate();
    this.serverRegistry = serverRegistry;
  }

  @PostMapping()
  public Map<String, Object> echoItem(@RequestBody Map<String, Object> item) {
    lock.lock();

    try {
      ServerInfo nextServer = roundRobinRouter.getNextServer();
      String serverAddress = nextServer.getServerAddress();

      Map<String, Object> response;
      nextServer.incrementRequestCount();
      response = restTemplate.postForObject(serverAddress, item, Map.class);
      nextServer.decrementRequestCount();

      return response;
    } catch (Exception e) {
      Arrays.stream(e.getStackTrace()).forEach(System.out::println);
      System.out.println("the error is ======> " + e.getMessage());
      return Map.of("error", e.getMessage());
    } finally {
      lock.unlock();
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
