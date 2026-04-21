FROM eclipse-temurin:17-jre

WORKDIR /app

COPY build/libs/servidor-dni-1.0.jar servidor-dni.jar

RUN mkdir -p dni_files

EXPOSE 9000

ENTRYPOINT ["java", "-jar", "servidor-dni.jar"]