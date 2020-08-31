FROM openjdk:8-jdk-alpine
RUN ls -la
RUN echo "Asia/Shanghai" > /etc/timezone
VOLUME /tmp
RUN date -R
ADD crm-server-0.0.1.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
EXPOSE 7786

