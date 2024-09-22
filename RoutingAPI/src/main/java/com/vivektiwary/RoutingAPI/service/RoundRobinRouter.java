package com.vivektiwary.RoutingAPI.service;

import com.vivektiwary.RoutingAPI.model.ServerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    ServerInfo[] servers = serverRegistry.getUpServers().toArray(new ServerInfo[0]);
    if (servers.length == 0) {
      throw new IllegalStateException("No healthy upstreams available");
    }
    int index = currentIndex.getAndIncrement() % servers.length;
    return servers[index];
  }
}
