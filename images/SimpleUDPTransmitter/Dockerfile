FROM openjdk:alpine
ARG FILE
ARG PORT
WORKDIR /home/
COPY ./transmitter.jar .
COPY ../../data/$FILE .
ENV PORT=${PORT}
ENV FILE=${FILE}
EXPOSE $PORT/udp
#RUN "sh"
CMD ["/usr/bin/java", "-jar", "/home/transmitter.jar"]