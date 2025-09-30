package com.example.camel_rest_project;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@CamelSpringBootTest
@SpringBootTest(
    properties = {
        "camel.component.kafka.brokers=localhost:9092"
    }
)
@ActiveProfiles("test")
@MockEndpoints
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ProcessingRouteTest {

    @Autowired
    private CamelContext camelContext;

    @Autowired
    private ProducerTemplate producerTemplate;

    @Test
    public void testSuccessScenario() throws Exception {
        AdviceWith.adviceWith(camelContext, "kafka-processing-route", a -> {
            a.replaceFromWith("direct:start");
            a.onException(Exception.class).to("mock:error");
        });

        MockEndpoint mockProcessedOrders = camelContext.getEndpoint("mock:kafka:processedOrders", MockEndpoint.class);
        mockProcessedOrders.expectedMessageCount(1);
        mockProcessedOrders.expectedBodiesReceived("Successful Order");

        producerTemplate.sendBody("direct:start", "Successful Order");

        mockProcessedOrders.assertIsSatisfied();
    }

    @Test
    public void testFailureScenario() throws Exception {
        AdviceWith.adviceWith(camelContext, "kafka-processing-route", a -> {
            a.replaceFromWith("direct:start-failure");
        });

        MockEndpoint mockDeadLetter = camelContext.getEndpoint("mock:kafka:dead-letter-topic", MockEndpoint.class);
        mockDeadLetter.expectedMessageCount(1);
        mockDeadLetter.expectedBodiesReceived("Order with fail");

        MockEndpoint mockProcessedOrders = camelContext.getEndpoint("mock:kafka:processedOrders", MockEndpoint.class);
        mockProcessedOrders.expectedMessageCount(0);

        producerTemplate.sendBody("direct:start-failure", "Order with fail");

        mockDeadLetter.assertIsSatisfied();
        mockProcessedOrders.assertIsSatisfied();
    }

    @Test
    public void testNoFailPayloadRoutesToProcessedOrders() throws Exception {
        AdviceWith.adviceWith(camelContext, "kafka-processing-route", a -> {
            a.replaceFromWith("direct:start-no-fail");
        });

        MockEndpoint mockProcessedOrders = camelContext.getEndpoint("mock:kafka:processedOrders", MockEndpoint.class);
        mockProcessedOrders.expectedMessageCount(1);
        mockProcessedOrders.expectedBodiesReceived("Another successful order");

        MockEndpoint mockDeadLetter = camelContext.getEndpoint("mock:kafka:dead-letter-topic", MockEndpoint.class);
        mockDeadLetter.expectedMessageCount(0);

        producerTemplate.sendBody("direct:start-no-fail", "Another successful order");

        mockProcessedOrders.assertIsSatisfied();
        mockDeadLetter.assertIsSatisfied();
    }

    @Test
    public void testFailPayloadRoutesToDeadLetterTopic() throws Exception {
        AdviceWith.adviceWith(camelContext, "kafka-processing-route", a -> {
            a.replaceFromWith("direct:start-fail-payload");
        });

        MockEndpoint mockDeadLetter = camelContext.getEndpoint("mock:kafka:dead-letter-topic", MockEndpoint.class);
        mockDeadLetter.expectedMessageCount(1);
        mockDeadLetter.expectedBodiesReceived("This order will fail");

        MockEndpoint mockProcessedOrders = camelContext.getEndpoint("mock:kafka:processedOrders", MockEndpoint.class);
        mockProcessedOrders.expectedMessageCount(0);

        producerTemplate.sendBody("direct:start-fail-payload", "This order will fail");

        mockDeadLetter.assertIsSatisfied();
        mockProcessedOrders.assertIsSatisfied();
    }
}