package com.sample.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("app.amqp")
@EnableConfigurationProperties
@Getter
@Setter
public class MyAmqpProperties {
  private String inputQueue;
  private int consumerRetryAttempts;
  private int consumerBackOffInterval;
  private int consumerBackOffMultiplier;
  private int consumerBackOffMaxInterval;
  private int concurrentConsumers;
  private int maxConcurrentConsumers;
  private String bulkAuthExchange;
  private String bulkAuthRoutingKey;
  private int producerRetryAttempts;
  private int prefetchCount;
}
