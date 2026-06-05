FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre

RUN apt-get update && apt-get install -y \
    python3 \
    python3-pip

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar
COPY ml-api ./ml-api

RUN pip3 install -r ml-api/requirements.txt

EXPOSE 8080
EXPOSE 5000

CMD sh -c "python3 ml-api/app.py & java -jar app.jar"