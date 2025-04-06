# Use a base image with OpenJDK Java 17 installed
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Install curl using apt-get
RUN apt-get update && apt-get install -y curl

# Define the URLs to fetch the JAR from Jenkins
# Replace <host-ip> with the actual IP of your host machine
ARG JOB_URL="http://192.168.1.23:8080/job/Coupon_Service_Automation"

ARG JENKINS_USER="root"
ARG JENKINS_PASSWORD="root"

# Try to downloading the latest successful build first, if it fails, fallback to the last build
RUN curl -u $JENKINS_USER:$JENKINS_PASSWORD -f -o app.jar "$JOB_URL/lastSuccessfulBuild/artifact/target/couponservice-0.0.1-SNAPSHOT.jar" \
    || curl -u $JENKINS_USER:$JENKINS_PASSWORD -o app.jar "$JOB_URL/lastBuild/artifact/target/couponservice-0.0.1-SNAPSHOT.jar"

# Ensure that the app.jar is executable
RUN chmod +x app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
