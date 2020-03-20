FROM openjdk:alpine
WORKDIR /home/
COPY . .
WORKDIR /home/serv/src/main/java/udp/transfer/
RUN /bin/sh