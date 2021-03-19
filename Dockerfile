FROM openjdk:8-alpine
ADD build/libs/twikor.jar app.jar
ENTRYPOINT [ "java", "-jar", "app.jar" ]