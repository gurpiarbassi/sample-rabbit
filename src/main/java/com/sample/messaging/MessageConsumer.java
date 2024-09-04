package com.sample.messaging;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageConsumer {

  @RabbitListener(queues = {"${app.amqp.input-queue}"},
      containerFactory = "myListenerFactory")
  public void receive(
      @Headers Map<String, String> messageHeaders, @Payload MessageDto messageDto) {
    log.info(" Message Received {}", messageDto);
  }

}
