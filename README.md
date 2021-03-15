# OAuth2 with Keycloak and Spring Boot Applications

This is an example of using OAuth2, Spring Security, Keycloak, PostgreSQL and Docker.

## Prerequisites

To make things work, you’ll need to make sure to add the following to your hosts file 
(/etc/hosts on Mac/Linux, c:\Windows\System32\Drivers\etc\hosts on Windows):

<code>127.0.0.1 keycloak</code>

This is because you will access your application with a browser on your machine 
(which name is localhost, or 127.0.0.1), but inside Docker it will run in its own containers.

See: https://stackoverflow.com/questions/51877246/docker-spring-boot-or-thorntail-and-keycloak

## How to run

<code>mvn package [-DskipTests]</code>  

<code>docker-compose build</code>

<code>docker-compose run --rm start-dependencies</code>

<code>docker-compose up -d</code>

## How to use

Client: http://localhost:8082  
User: demo  
Password: password

Keycloak admin console: http://localhost:8080  
User: admin  
Password: password
