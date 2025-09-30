package com.example.camel_rest_project;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ProcessingRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        errorHandler(deadLetterChannel("kafka:dead-letter-topic")
            .useOriginalMessage()
            .maximumRedeliveries(2)
            .redeliveryDelay(1000));

        from("kafka:incomingOrders")
            .routeId("kafka-processing-route")
            .log("Received a new order from Kafka: ${body}")
            .choice()
                .when(body().contains("fail"))
                    .log("Simulating a processing failure")
                    .throwException(new RuntimeException("Simulated business exception"))
            .end()

            .log("Order processed successfully")
            .to("kafka:processedOrders");
    }
}