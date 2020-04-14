FROM adoptopenjdk/openjdk11:ubi
RUN mkdir /opt/app
COPY . /opt/app/
WORKDIR /opt/app/
RUN /bin/sh -c "./mvnw clean package -DskipTests"
CMD ["java", "-jar", "target/trading-0.0.1-SNAPSHOT.jar"]
