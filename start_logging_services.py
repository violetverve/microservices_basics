import subprocess
import threading

# to kill instances:
# pkill -f 'java -jar logging-service/target/logging-service-0.0.1-SNAPSHOT.jar'


def start_logging_service(port):
    command = f"java -jar logging-service/target/logging-service-0.0.1-SNAPSHOT.jar --server.port={port}"
    print(f"Starting logging service on port {port} with command: {command}")
    subprocess.Popen(command, shell=True)


num_instances = 3
starting_port = 8082

threads = []

for i in range(num_instances):
    current_port = starting_port + i
    thread = threading.Thread(target=start_logging_service, args=(current_port,))
    thread.start()
    threads.append(thread)

for thread in threads:
    thread.join()

print("All logging service instances started.")
