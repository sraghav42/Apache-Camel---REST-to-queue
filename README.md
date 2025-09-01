# REST to In-Memory Queue with Apache Camel & Spring Boot

This project is a simple, containerized microservice that demonstrates a common integration pattern using Apache Camel. It exposes a REST API endpoint that accepts JSON data, sends it to an asynchronous in-memory queue, and processes it.

## Features

  * **REST API:** Exposes a `POST /api/submitOrder` endpoint to accept new order data.
  * **Asynchronous Processing:** Uses an in-memory SEDA queue to decouple the API from the message processing, a fundamental microservice pattern.
  * **Containerized:** Fully containerized using Docker for easy deployment and portability.
  * **Built with Apache Camel:** Showcases the power and simplicity of Camel's Java DSL for defining integration routes.

-----

## Technologies Used

  * **Java 23**
  * **Spring Boot 3.2.5**
  * **Apache Camel 4.4.2**
  * **Maven**
  * **Docker**

-----

## Prerequisites

Before you begin, ensure you have the following installed on your system:

  * [Java Development Kit (JDK) 23 or later](https://www.oracle.com/java/technologies/downloads/)
  * [Apache Maven](https://maven.apache.org/download.cgi)
  * [Docker Desktop](https://www.docker.com/products/docker-desktop/)

-----

## How to Run

Follow these steps to build and run the application in a Docker container.

### 1\. Clone the Repository

```bash
git clone <your-github-repository-url>
cd camel-rest-project
```

### 2\. Package the Application

Use the Maven wrapper to build the application's `.jar` file.

```bash
./mvnw clean package
```

### 3\. Build the Docker Image

Build the container image using the provided `Dockerfile`.

```bash
docker build -t camel-rest-project .
```

### 4\. Run the Docker Container

Run the application inside a new container. This command maps port 8080 on your local machine to port 8080 in the container.

```bash
docker run -d -p 8080:8080 camel-rest-project
```

-----

## How to Use

The application is now running and ready to accept requests.

### Send a Test Request

You can use a tool like Postman or the `curl` command to send a `POST` request to the endpoint.

```bash
curl -X POST -H "Content-Type: application/json" \
-d '{"orderId": "123", "product": "Awesome Camel Book", "quantity": 1}' \
http://localhost:8080/api/submitOrder
```

### Check the Logs

To confirm the message was processed, you can view the logs from the running container. First, get the container ID:

```bash
docker ps
```

Then, view the logs using that ID:

```bash
docker logs <your-container-id>
```

You should see a log entry similar to this:
`INFO --- [ seda://orderQueue] queue-processing-route : Received a new order: {"orderId": "123", "product": "Awesome Camel Book", "quantity": 1}`
