# Microservices Basics

## Architecture

The architecture consists of three microservices:
- **facade-service** - accepts POST/GET requests from the client
- **logging-service** - stores all the messages it receives and can return them
- **messages-service** - it returns a static message when addressed


## Prerequisites

- **Java Development Kit (JDK)**
- **Maven**
- **Spring Boot**

## Usage

Run each application in its separate directory. Use `GET/POST` requests with `Requests.http` or any other preferred method.
