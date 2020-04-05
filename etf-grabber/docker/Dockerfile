FROM openjdk:14-jdk-slim

ENV APP_NAME etf-grabber

ENV JAVA_OPTS -Xms128m -Xmx700m
ENV TZ Europe/Moscow

RUN rm /etc/localtime && \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime

COPY $APP_NAME.jar /app/$APP_NAME.jar

WORKDIR /app

CMD java $JAVA_OPTS $JMX_OPTS -jar /app/$APP_NAME.jar \
    --spring.config.location=file:/app/configs/application.yml
