FROM openjdk:alpine
WORKDIR /home/
COPY . .
EXPOSE 3312/udp
#RUN "sh"
CMD ["/usr/bin/java", "-jar", "/home/transfer.jar"]