FROM eclipse-temurin:21.0.8_9-jre-ubi9-minimal

RUN microdnf update -y && \
    microdnf reinstall tzdata -y

ARG UID=1001
RUN adduser -u $UID modelfinder 

ENV HOME=/application
WORKDIR /application

COPY tmp/interlis-mcp.jar ./application.jar

RUN chown $UID:0 . && \
    chmod 0775 . && \
    ls -la
    
ENTRYPOINT ["java", "-jar", "/application/application.jar"]