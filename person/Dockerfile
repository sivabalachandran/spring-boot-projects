FROM openjdk:12.0.2

LABEL maintainer = 'Siva Balachandran'

ADD target/*.jar /usr/local/app.jar

WORKDIR /usr/local/

EXPOSE 9000

ENTRYPOINT ["java", "-jar", "app.jar"]