# Microservices with Messaging queue

## Architecture

The architecture consists of three microservices:
- **facade-service** - accepts POST/GET requests from the client
- **logging-service** - responsible for logging messages, utilizes Hazelcast Distributed Map for storing messages, enabling data to remain consistent and accessible across multiple instances.
- **messages-service** - stores messages, receives them though message queue as a consumer, facade-service - producer.

![image](https://github.com/violetverve/software_architecture/assets/92580927/6714a941-068c-44a2-a0b4-4b35990fa621)

## Prerequisites

- **Java Development Kit (JDK)**
- **Maven**
- **Spring Boot**
- **Hazelcast**

## Usage

Run each application in its separate directory. Use `GET/POST` requests with `Requests.http` or any other preferred method. 
