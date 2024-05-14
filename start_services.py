import subprocess
import threading
import os

# to build microservices:
# path/to/microservice$ mvn clean package
# Example:
# yf@yf:~/IdeaProjects/microservices_basics/messages-service$ mvn clean package

# To kill instances:
# pkill -f 'java -jar <service-name>/target/<service-name>-0.0.1-SNAPSHOT.jar'
# Example:
# pkill -f 'java -jar messages-service/target/messages-service-0.0.1-SNAPSHOT.jar'
# pkill -f 'java -jar logging-service/target/logging-service-0.0.1-SNAPSHOT.jar'


def start_service(service_name, port):
    jar_path = f"{service_name}/target/{service_name}-0.0.1-SNAPSHOT.jar"
    if not os.path.exists(jar_path):
        print(f"Error: JAR file for {service_name} not found at {jar_path}. Please ensure the service is built.")
        return

    command = f"java -jar {jar_path} --server.port={port}"
    print(f"Starting {service_name} on port {port} with command: {command}")
    subprocess.Popen(command, shell=True)


def start_multiple_services(service_name, num_instances, starting_port):
    threads = []
    for i in range(num_instances):
        current_port = starting_port + i
        thread = threading.Thread(target=start_service, args=(service_name, current_port))
        thread.start()
        threads.append(thread)

    for thread in threads:
        thread.join()

    print(f"All {service_name} instances started.")


# Start logging service instances
start_multiple_services("logging-service", 3, 8081)

# Start messages service instances
start_multiple_services("messages-service", 2, 8084)