package com.example.camel_rest_project;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class RestRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        rest("/api")
            .post("/submitOrder")
            .to("kafka:incomingOrders");
    }
}