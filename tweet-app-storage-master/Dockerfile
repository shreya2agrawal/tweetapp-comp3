FROM openjdk:11
EXPOSE 8083
ADD target/tweet-storage-app.jar tweet-storage-app.jar
ENTRYPOINT ["java", "-jar", "/tweet-storage-app.jar"]