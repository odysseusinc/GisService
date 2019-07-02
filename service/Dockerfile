FROM openjdk:8u212-jdk
MAINTAINER vitaly.koulakov@odysseusinc.com

RUN mkdir /usr/share/gis-service
ARG JAR_FILE
ADD target/${JAR_FILE} /usr/share/gis-service/gis-service.jar

ENTRYPOINT ["java", "-jar", "/usr/share/gis-service/gis-service.jar"]