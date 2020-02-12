FROM openjdk:11-jdk-slim
ARG jar
COPY $jar /opt/census-fsdr-acceptance-tests.jar
ENV JAVA_OPTS=""
ENTRYPOINT [ "java",  "-jar", "/opt/census-fsdr-acceptance-tests.jar" ]
