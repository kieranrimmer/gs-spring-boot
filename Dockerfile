FROM openjdk:8-jdk-stretch
COPY . /usr/src/myapp

EXPOSE 8080/tcp

WORKDIR /usr/src/myapp/obfuscation/target
CMD java -jar gs-spring-boot-0.1.1.jar --spring.profiles.active=dev