package com.example.camel_rest_project;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class MyCamelRoutes extends RouteBuilder{
    @Override
    public void configure() throws Exception {
        rest("/api")
            .post("/submitOrder")
            .to("seda:orderQueue");
        
        from("seda:orderQueue")
            .routeId("queue-processing-route")
            .log("Received a new order :${body}");
    }
}
