FROM openjdk:8-alpine
MAINTAINER Mikhail Karpov <mikhailkarpov@hotmail.com>

COPY ./target/*.jar /todo-client/

EXPOSE 8082

CMD ["java", "-jar", "/todo-client/todo-client.jar"]

