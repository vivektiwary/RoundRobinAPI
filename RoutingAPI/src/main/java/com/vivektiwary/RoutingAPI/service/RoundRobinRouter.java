package com.vivektiwary.RoutingAPI.service;

import com.vivektiwary.RoutingAPI.model.ServerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RoundRobinRouter {
  private final ServerRegistry serverRegistry;
  private final AtomicInteger currentIndex = new AtomicInteger(0);

  @Autowired
  public RoundRobinRouter(ServerRegistry serverRegistry) {
    this.serverRegistry = serverRegistry;
  }

  public ServerInfo getNextServer() {
    List<ServerInfo> servers = serverRegistry.getUpServers().stream().toList();

    if (servers.isEmpty()) {
      System.out.println("all servers == " + serverRegistry.getServers().stream().toList());
      throw new IllegalStateException("No healthy upstreams available");
    }

    System.out.println("up servers === " + servers);
    System.out.println("currentIndex === " + currentIndex);
    int index = currentIndex.getAndUpdate(i -> (i + 1) % servers.size());
    return servers.get(index);
  }
}
