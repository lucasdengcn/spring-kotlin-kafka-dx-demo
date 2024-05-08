# https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.container-images
FROM mcr.microsoft.com/openjdk/jdk:21-ubuntu as builder
WORKDIR /app
ARG JAR_FILE=build/libs/*-boot.jar
COPY ${JAR_FILE} application.jar

ENV JAVA_OPTS="-server -Xss256k"
RUN java ${JAVA_OPTS} -Djarmode=layertools -jar application.jar extract

FROM mcr.microsoft.com/openjdk/jdk:21-ubuntu
WORKDIR /app
COPY --from=builder /app/dependencies/ ./
COPY --from=builder /app/spring-boot-loader/ ./
COPY --from=builder /app/snapshot-dependencies/ ./
COPY --from=builder /app/application/ ./
COPY lib ./lib
COPY bin/entrypoint.sh ./

RUN chmod +x ./entrypoint.sh

EXPOSE $APP_PORT

ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

CMD sh ./entrypoint.sh


