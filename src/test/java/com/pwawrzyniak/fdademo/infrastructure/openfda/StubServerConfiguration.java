package com.pwawrzyniak.fdademo.infrastructure.openfda;

import com.xebialabs.restito.server.StubServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.MapPropertySource;
import org.springframework.test.util.TestSocketUtils;

import java.util.Map;

class StubServerConfiguration {

  private static final String STUB_SERVER_PROPERTY_SOURCE_NAME = "stubServerPropertySource";

  @Bean
  StubServer stubServer(@Value("${stub.server.port}") int stubServerPort) {
    return new StubServer(stubServerPort).run();
  }

  @Bean
  StubServerEventListener stubServerEventListener(StubServer stubServer) {
    return new StubServerEventListener(stubServer);
  }

  static class StubServerPropertySourceInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
      int availableTcpPort = TestSocketUtils.findAvailableTcpPort();
      applicationContext.getEnvironment().getPropertySources().addFirst(new MapPropertySource(STUB_SERVER_PROPERTY_SOURCE_NAME,
          Map.of("stub.server.port", availableTcpPort,
              "application.open-fda.base-url", "http://localhost:${stub.server.port}")));
    }
  }

  static class StubServerEventListener {

    private final StubServer stubServer;

    StubServerEventListener(StubServer stubServer) {
      this.stubServer = stubServer;
    }

    @EventListener
    public void contextClosedEvent(ContextClosedEvent event) {
      stubServer.stop();
    }
  }
}