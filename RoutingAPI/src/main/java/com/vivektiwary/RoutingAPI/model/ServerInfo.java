package com.vivektiwary.RoutingAPI.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServerInfo {
  private String serverAddress;
  private Status status;
  private Long lastUpdatedAt;
  private AtomicInteger requestCount = new AtomicInteger(0);

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ServerInfo info = (ServerInfo) o;
    return serverAddress.equals(info.getServerAddress());
  }

  @Override
  public int hashCode() {
    return Objects.hash(serverAddress);
  }

  public void incrementRequestCount() {
    requestCount.incrementAndGet();
  }

  public void decrementRequestCount() {
    requestCount.decrementAndGet();
  }

  public AtomicInteger getRequestCount() {
    return requestCount;
  }

  public String toString() {
    return "ServerInfo(serverAddress=" + this.getServerAddress() + ", status=" + this.getStatus() + ", lastUpdatedAt=" + this.getLastUpdatedAt() + ", requestCount=" + this.getRequestCount() + ")";
  }
}
