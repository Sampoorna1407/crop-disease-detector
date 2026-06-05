FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre

RUN apt-get update && apt-get install -y \
    python3 \
    python3-pip \
    python3-venv

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar
COPY ml-api ./ml-api

RUN python3 -m venv /opt/venv
ENV PATH="/opt/venv/bin:$PATH"

RUN pip install --upgrade pip
RUN pip install -r ml-api/requirements.txt

EXPOSE 8080
EXPOSE 5000

CMD sh -c "python3 ml-api/app.py & java -jar app.jar"