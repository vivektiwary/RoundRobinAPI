package com.vivektiwary.SimpleAPI.config;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ServerRegistrar implements DisposableBean {

  @Value("${routing-app.gateway.url}")
  private String gatewayUrl;

  @Value("${routing-app.gateway.register}")
  private String registrationPath;

  @Value("${routing-app.gateway.deregister}")
  private String deregisterPath;

  @Value("${server.port}")
  private String serverPort;

  @Value("${server.host}")
  private String serverHost;

  private final RestTemplate restTemplate = new RestTemplate();

  @Bean
  public ApplicationRunner registerServer() {
    return args -> {
      String registrationUrl = gatewayUrl + registrationPath;
      String serverAddress = serverHost + ":" + serverPort;

      System.out.println("the server address is === " + serverAddress);

      try {
        restTemplate.postForObject(registrationUrl, serverAddress, String.class);
        System.out.println("Server registered successfully with gateway");
      } catch (Exception e) {
        System.err.println("Failed to register server: " + e.getMessage());
      }


      Runtime.getRuntime().addShutdownHook(new Thread(this::deregisterServer));
    };
  }

  @Override
  public void destroy() {
    deregisterServer();
  }

  private void deregisterServer() {
    String deregistrationUrl = gatewayUrl + deregisterPath;
    String serverAddress = serverHost + ":" + serverPort;

    try {
      restTemplate.postForObject(deregistrationUrl, serverAddress, String.class);
      System.out.println("Server deregistered successfully from gateway");
    } catch (Exception e) {
      System.err.println("Failed to deregister server: " + e.getMessage());
    }
  }
}
