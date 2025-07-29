package com.denemestaj.stajproje1.consumer;

import com.denemestaj.stajproje1.service.EnergyDataService;
import com.denemestaj.stajproje1.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class Consumer {

    private final EnergyDataService energyDataService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void consume(String base64Message) {
        System.out.println("rabbitMQ'dan mesaj alındı (Base64):\n" + base64Message);


        byte[] decodedBytes = Base64.getDecoder().decode(base64Message);
        String decodedMessage = new String(decodedBytes, StandardCharsets.UTF_8);
        System.out.println("decode edilmiş mesaj:\n" + decodedMessage);


        energyDataService.parseAndSave(decodedMessage, base64Message);
    }
}
