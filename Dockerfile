FROM gradle:5.4.1-jdk11

RUN mkdir /opt/census-fsdr-acceptance-tests
COPY . /opt/census-fsdr-acceptance-tests

WORKDIR /opt/census-fsdr-acceptance-tests
ENTRYPOINT [ "./gradlew", "--stacktrace",  "test" ]
