# OAuth2 with Keycloak and Spring Boot Applications

This is an example of using OAuth2, Spring Security, Keycloak, PostgreSQL and Docker.

## How to run

To make things work, you’ll need to make sure to add the following to your hosts file 
(/etc/hosts on Mac/Linux, c:\Windows\System32\Drivers\etc\hosts on Windows):

127.0.0.1 keycloak

This is because you will access your application with a browser on your machine 
(which name is localhost, or 127.0.0.1), but inside Docker it will run in its own container, 
which name is keycloak.

See: https://stackoverflow.com/questions/51877246/docker-spring-boot-or-thorntail-and-keycloak

After restart, launch the application using Maven in Docker:

<code>mvn clean package [-DskipTests]</code>
</br>
<code>docker-compose up</code>

## How to use

Client: http://localhost:8082  
User: demo  
Password: password

Keycloak admin console: http://localhost:8080  
User: admin  
Password: password
