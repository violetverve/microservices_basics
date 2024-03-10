# Microservices with Hazelcast Distributed Map

## Architecture

The architecture consists of three microservices:
- **facade-service** - accepts POST/GET requests from the client
- **logging-service** - responsible for logging messages, utilizes Hazelcast Distributed Map for storing messages, enabling data to remain consistent and accessible across multiple instances.
- **messages-service** - it returns a static message when addressed

## Prerequisites

- **Java Development Kit (JDK)**
- **Maven**
- **Spring Boot**
- **Hazelcast**

## Usage

Run each application in its separate directory. Use `GET/POST` requests with `Requests.http` or any other preferred method. 
