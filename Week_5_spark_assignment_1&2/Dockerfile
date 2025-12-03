# Use a Scala and sbt based image
FROM hseeberger/scala-sbt:11.0.11_1.5.5_2.13.6 as builder

# Set the working directory
WORKDIR /app

# Copy the build file and other necessary sbt files
COPY build.sbt /app/
COPY project /app/project

# Fetch all dependencies
RUN sbt update

# Copy the source code into the image
COPY src /app/src

# Compile the application
RUN sbt compile

# Runtime stage
FROM openjdk:11-jre-slim

WORKDIR /app

# Copy the built binaries from the builder stage
COPY --from=builder /app/target /app/target
COPY --from=builder /app/src /app/src

# Define the command to run the application
CMD ["sbt", "run"]
