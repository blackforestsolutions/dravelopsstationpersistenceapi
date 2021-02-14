FROM openjdk:11-jre-slim
VOLUME /tmp
ADD target/dravelopsstationpersistenceapi-*.jar /myapp.jar
RUN sh -c 'touch /myapp.jar'
ENV JAVA_OPTS="-Xms256M -Xmx1G -XX:MaxPermSize=512m"
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/myapp.jar"]
