FROM openjdk:21-ea-oraclelinux8

WORKDIR /app

COPY app.jar app.jar

EXPOSE 8181

ENTRYPOINT ["java", "-jar", "app.jar"]