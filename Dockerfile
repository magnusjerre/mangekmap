FROM gradle:7.5.1-jdk17 as build

COPY ./kotlin-js-store /home/gradle/kotlin-js-store
COPY ./src /home/gradle/src
COPY ./build.gradle.kts /home/gradle/
COPY ./settings.gradle.kts /home/gradle/
COPY ./gradle.properties /home/gradle

WORKDIR /home/gradle
RUN gradle jsBrowserDistribution
RUN gradle bootJar --no-daemon



FROM eclipse-temurin:11 as app
RUN mkdir /opt/app

COPY --from=build /home/gradle/build/libs/*.jar /opt/app/app.jar

EXPOSE 8080:8080

CMD ["java", "-jar", "/opt/app/app.jar"]