FROM gradle:5.4.1-jdk11

RUN mkdir /opt/census-fsdr-acceptance-tests
COPY . /opt/census-fsdr-acceptance-tests

ENV JAVA_OPTS=""
WORKDIR /opt/census-fsdr-acceptance-tests
ENTRYPOINT [ "./gradlew",  "test" ]
