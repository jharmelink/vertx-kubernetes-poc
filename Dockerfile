FROM adoptopenjdk/openjdk12:alpine-jre

RUN mkdir /opt/app

COPY target/app.jar /opt/app

EXPOSE 8080

CMD ["java", "-jar", "/opt/app/app.jar", "-k"]
