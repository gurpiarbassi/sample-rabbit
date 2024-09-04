package com.sample.config;

import java.util.Collections;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.classify.BinaryExceptionClassifier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.BackOffPolicyBuilder;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@ConfigurationProperties(prefix = "spring.rabbitmq")
@Setter
public class RabbitMqConfig extends RabbitAutoConfiguration {
  private final MyAmqpProperties myAmqpProperties;
  private String username;
  private String password;
  private String host;
  private int port;
  private String virtualHost;


  public RabbitMqConfig(MyAmqpProperties properties) {
    this.myAmqpProperties = properties;
  }

  @Bean
  @RefreshScope
  public ConnectionFactory connectionFactory() {
    CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
    connectionFactory.setUsername(username);
    connectionFactory.setPassword(password);
    connectionFactory.setVirtualHost(virtualHost);
    connectionFactory.setHost(host);
    connectionFactory.setPort(port);
    return connectionFactory;
  }


  @Bean("myListenerFactory")
  public SimpleRabbitListenerContainerFactory
      rabbitListenerContainerFactory(ConnectionFactory connectionFactory,
                                 MessageConverter messageConverter,
                                 RetryOperationsInterceptor messageRetryInterceptor,
                                 SimpleRabbitListenerContainerFactoryConfigurer configurer,
                                 PlatformTransactionManager platformTransactionManager) {

    var factory = new SimpleRabbitListenerContainerFactory();
    configurer.configure(factory, connectionFactory);
    factory.setMessageConverter(messageConverter);
    factory.setDefaultRequeueRejected(false);
    factory.setPrefetchCount(myAmqpProperties.getPrefetchCount());
    factory.setConcurrentConsumers(myAmqpProperties.getConcurrentConsumers());
    factory.setMaxConcurrentConsumers(myAmqpProperties.getMaxConcurrentConsumers());
    factory.setAdviceChain(messageRetryInterceptor);
    factory.setChannelTransacted(true);
    factory.setTransactionManager(platformTransactionManager);
    return factory;
  }

  @Bean
  public RetryOperationsInterceptor messageRetryInterceptor() {
    var retryTemplate = new RetryTemplate();
    var classifier = new BinaryExceptionClassifier(Collections.emptyMap(), true, true);
    var simpleRetryPolicy = new SimpleRetryPolicy(myAmqpProperties.getConsumerRetryAttempts(),
        classifier);
    var backOffPolicy = BackOffPolicyBuilder.newBuilder()
        .delay(myAmqpProperties.getConsumerBackOffInterval())
        .maxDelay(myAmqpProperties.getConsumerBackOffMaxInterval())
        .multiplier(myAmqpProperties.getConsumerBackOffMultiplier()).build();

    retryTemplate.setRetryPolicy(simpleRetryPolicy);
    retryTemplate.setBackOffPolicy(backOffPolicy);
    return RetryInterceptorBuilder
        .stateless()
        .retryOperations(retryTemplate)

        .build();
  }


  @Bean
  public MessageConverter converter() {
    return new Jackson2JsonMessageConverter();
  }


  @Bean
  @Primary
  public AmqpTemplate amqpTemplate(
      ConnectionFactory connectionFactory, MessageConverter messageConverter, RetryTemplate retryTemplate) {
    var template = new RabbitTemplate();
    template.setConnectionFactory(connectionFactory);
    template.setMessageConverter(messageConverter);
    template.setRetryTemplate(retryTemplate);
    return template;
  }


  @Bean
  public RetryTemplate retryTemplate(MyAmqpProperties myAmqpProperties) {
    var retryTemplate = new RetryTemplate();
    RetryPolicy retryPolicy = new SimpleRetryPolicy(myAmqpProperties.getProducerRetryAttempts());
    retryTemplate.setRetryPolicy(retryPolicy);

    return retryTemplate;
  }
}
