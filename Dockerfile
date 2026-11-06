# ---------------------------------------------------------------
# Backend image for Render (free plan: 512 MB RAM)
# Stage 1 builds the jar with Maven, stage 2 only runs it (smaller image).
# ---------------------------------------------------------------

# ---------- Stage 1: build ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Download dependencies first (cached between builds while pom.xml is unchanged)
COPY pom.xml .
RUN mvn -q -B dependency:go-offline

COPY src ./src
RUN mvn -q -B -DskipTests package && cp target/*.jar app.jar

# ---------- Stage 2: run ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Run as a normal user, not root
RUN useradd --system --create-home appuser
USER appuser

COPY --from=build /app/app.jar app.jar

# Memory limits so Java fits inside Render's 512 MB and doesn't get killed:
#   MaxRAMPercentage=75   -> Java heap uses at most 75% of the container's memory
#   UseSerialGC           -> simplest garbage collector, lowest memory overhead
#   Xss512k               -> smaller memory per thread
#   MaxMetaspaceSize      -> cap for class metadata
#   TieredStopAtLevel=1   -> faster start-up, less memory spent on compiling
# You can override everything with a JAVA_OPTS environment variable on Render.
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+UseSerialGC -Xss512k -XX:MaxMetaspaceSize=128m -XX:TieredStopAtLevel=1 -XX:+ExitOnOutOfMemoryError"

# Render sets PORT (usually 10000); the app reads it (server.port=${PORT:8080})
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
