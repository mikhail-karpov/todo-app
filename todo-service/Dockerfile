FROM openjdk:8-alpine
MAINTAINER Mikhail Karpov <mikhailkarpov@hotmail.com>

COPY ./target/*.jar /app/
CMD ["java", "-jar", "/app/todo-service.jar"]

EXPOSE 8081