FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

FROM python:3.11-slim

RUN apt-get update && apt-get install -y openjdk-17-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar
COPY ml-api ./ml-api

RUN pip install --upgrade pip
RUN pip install -r ml-api/requirements.txt

EXPOSE 8080
EXPOSE 5000

CMD sh -c "python ml-api/app.py & java -jar app.jar"