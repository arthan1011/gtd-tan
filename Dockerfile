# after 'gradle bootRepackage'
FROM openjdk:8-jre
WORKDIR /app
ADD ./build/libs/gtd-tan.jar /app
EXPOSE 8080
CMD ["java", "-jar", "gtd-tan.jar"]
