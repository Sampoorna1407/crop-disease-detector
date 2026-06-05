FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk

WORKDIR /app

RUN apt-get update && apt-get install -y \
    python3 \
    python3-pip \
    python3-venv

COPY --from=build /app/target/*.jar app.jar
COPY ml-api ./ml-api

RUN python3 -m venv /opt/venv
ENV PATH="/opt/venv/bin:$PATH"

RUN pip install --upgrade pip

RUN pip install \
    flask==3.0.3 \
    numpy==1.26.4 \
    pillow==10.3.0 \
    gunicorn==22.0.0 \
    tensorflow-cpu==2.16.1

EXPOSE 8080

CMD sh -c "python ml-api/app.py & java -jar app.jar"