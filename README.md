# **Event-Driven Microservice with Apache Camel and Kafka**

This project is a containerized, event-driven microservice that demonstrates a common and robust enterprise integration pattern. It uses Apache Camel to expose a REST API that asynchronously processes orders by publishing them as events to an Apache Kafka topic.

The application is built to be resilient, featuring a Dead Letter Channel (DLC) error handling pattern to safely quarantine messages that fail processing after multiple retries.

## **Features**

* **REST API:** Exposes a POST /api/submitOrder endpoint to accept new order data.  
* **Event-Driven:** Uses Apache Kafka as a durable and scalable message broker to decouple the API from the backend processing.  
* **Resilient:** Implements an enterprise error handling pattern (Dead Letter Channel) to ensure failed messages are not lost.  
* **Containerized:** The entire required infrastructure (Apache Kafka) is defined in a docker-compose.yml file for easy, one-command startup.

## **Technologies Used**

* **Java 17**  
* **Spring Boot 3.2.5**  
* **Apache Camel 4.4.2**  
* **Apache Kafka**  
* **Maven**  
* **Docker & Docker Compose**

## **Prerequisites**

Before you begin, ensure you have the following installed on your system:

* [Java Development Kit (JDK) 17 or later](https://adoptium.net/)  
* [Docker Desktop](https://www.docker.com/products/docker-desktop/)

## **How to Run the System**

This is a multi-part system consisting of the Kafka infrastructure and the Camel application. They must be started in order.

### **1\. Start the Kafka Infrastructure**

In a terminal at the root of the project, run the following command. This will start an Apache Kafka broker in a Docker container.

docker-compose up \-d

Give the container a minute to initialize fully. You can check its status with docker ps.

### **2\. Run the Camel Application**

In a **new, separate terminal**, navigate to the project root and run the application using the Maven wrapper.

./mvnw spring-boot:run

The application will start up and connect to the Kafka broker running in Docker.

## **How to Use**

The system is now running and ready to accept requests.

### **1\. Send a Test Request**

You can use a tool like Postman or curl to send a POST request to the endpoint.

**To send a successful order:**

curl \-X POST \-H "Content-Type: application/json" \\  
\-d '{"orderId": "KAFKA-001", "product": "Kafka Book"}' \\  
http://localhost:8080/api/submitOrder

**To send an order that will fail:**

curl \-X POST \-H "Content-Type: application/json" \\  
\-d '{"orderId": "KAFKA-999", "product": "This will fail"}' \\  
http://localhost:8080/api/submitOrder

### **2\. Check the Application Logs**

In the terminal where your Camel application is running, you will see log output indicating that the messages have been received from Kafka and processed.

### **3\. (Optional) View Messages Directly in Kafka**

To see the raw messages in the Kafka topics:

1. **Find the Kafka container name:** docker ps (it will be something like camel-rest-project-kafka-1).  
2. **Connect to the container:** docker exec \-it \<your-kafka-container-name\> /bin/bash  
3. **Run a console consumer:**  
   * To see successful orders:  
     kafka-console-consumer.sh \--bootstrap-server localhost:9092 \--topic processedOrders \--from-beginning

   * To see failed orders (in the Dead Letter Channel):  
     kafka-console-consumer.sh \--bootstrap-server localhost:9092 \--topic dead-letter-topic \--from-beginning

To stop the system, press Ctrl+C in the application terminal and then run docker-compose down in the first terminal.
