# TweetApp-Backend


**Working with Swagger and Actuator :-**

1.	https://stackoverflow.com/questions/70036953/springboot-2-6-0-spring-fox-3-failed-to-start-bean-documentationpluginsboot

2.	springfox/springfox#3462 (comment)

# To build a docker image use command:

  docker build -t tweet-data-app .
  docker build -t docker-image-name-you-want .
  
# To Run the docker image as container use command:
  
  docker run -p 8082:8082 tweet-data-app
  docker run -p 8082:8080 name-of-image-you-created-above

# To Create and add docker image to local docker desktop using springboot command:
  mvn spring-boot:build-image
