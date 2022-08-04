package com.tweetapp.controller;

import com.mongodb.MongoWriteException;
import com.tweetapp.exception.InvalidTweetMessageException;
import com.tweetapp.exception.InvalidUserCredentialsException;
import com.tweetapp.exception.TweetNotFoundException;
import com.tweetapp.exception.UserNotFoundException;
import com.tweetapp.model.Tweet;
import com.tweetapp.model.User;
import com.tweetapp.model.UserCredentials;
import com.tweetapp.service.TweetService;
import com.tweetapp.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/v1.0/tweets")
public class TweetAppController {

    @Autowired
    TweetService tweetService;

    @Autowired
    UserService userService;

    @GetMapping("/all")
    @ApiOperation(value = "Get list of all tweets",
            produces = "application/json",
            response = ResponseEntity.class)
    public ResponseEntity<List<Tweet>> getAllTweets() {

        return ResponseEntity.ok(tweetService.getListOfAllTweets());
    }

    @GetMapping("/users/all")
    @ApiOperation(value = "Get list of all users",
            produces = "application/json",
            response = ResponseEntity.class)
    public ResponseEntity<List<User>> getAllUsers() {

        return ResponseEntity.ok(userService.getListOfAllUsers());
    }

    @GetMapping("/replies/{id}")
    @ApiOperation(value = "Get list of all replies",
            notes = "{id} => Tweet ID to get the replies for.",
            produces = "application/json",
            response = ResponseEntity.class)
    public ResponseEntity<List<Tweet>> getAllReplies(@PathVariable("id") String id) {

        return ResponseEntity.ok(tweetService.getListOfAllReplies(id));
    }

    @GetMapping("/users/{username}")
    @ApiOperation(value = "Get user details from username",
            consumes = "application/json",
            produces = "text/plain",
            response = ResponseEntity.class)
    public ResponseEntity<User> getUserByUsername(@PathVariable("username") String username) throws UserNotFoundException {
        Optional<User> user =  userService.findUserByUsername(username);

        if (user.isPresent())
            return ResponseEntity.ok(user.get());
        else
            throw new UserNotFoundException("Requested User records does not exist.");
    }

    @GetMapping("/{username}")
    @ApiOperation(value = "Get list of all tweets posted by {username}",
            produces = "application/json",
            response = ResponseEntity.class)
    public ResponseEntity<List<Tweet>> getAllTweetsByUsername(@PathVariable("username") String username) throws UserNotFoundException {
        Optional<User> user =  userService.findUserByUsername(username);

        if (user.isPresent()) {
            String userId = user.get().getId();
            List<Tweet> tweets = tweetService.findAllTweetsByUserId(userId);
            return ResponseEntity.ok(tweets);
        }
        else
            throw new UserNotFoundException("Requested User records does not exist.");
    }

    @PostMapping("/{username}/add")
    @ApiOperation(value = "Post a new tweet for {username}",
            notes = "Request body should contain only **message** and **listOfTags** only.",
            consumes = "application/json",
            produces = "application/json",
            response = ResponseEntity.class)
    public ResponseEntity<Tweet> postTweetByUsername(@RequestBody Tweet tweet, @PathVariable("username") String username) throws UserNotFoundException, InvalidTweetMessageException {
        Optional<User> user = userService.findUserByUsername(username);
        if(user.isPresent()) {
            tweet.setTweetedAt(LocalDateTime.now());
            Tweet newTweet = tweetService.postANewTweet(tweet, user.get());
            String confirmation = tweetService.sendTweetAddedMessage(newTweet);
            // log confirmation
            System.out.println(confirmation);
            return ResponseEntity.ok(newTweet);
        }
        else
            throw new UserNotFoundException("Requested User records does not exist.");
    }

    @PutMapping("/{username}/update/{id}")
    @ApiOperation(value = "Update a tweet made by {username}",
            notes = "Request body should contain only **message** and **listOfTags** only.\n\n{username} => Username of the user who posted the initial tweet.\n\n{id} => Tweet ID of the tweet to be updated.",
            consumes = "application/json",
            produces = "application/json",
            response = ResponseEntity.class)
    public ResponseEntity<Tweet> updateTweetByUsername(@RequestBody Tweet tweet, @PathVariable("username") String username, @PathVariable("id") String id) throws InvalidTweetMessageException, TweetNotFoundException, UserNotFoundException {
        Optional<User> user = userService.findUserByUsername(username);
        if(user.isPresent()) {
            tweet.setId(id);
            Tweet updatedTweet = tweetService.updateTweet(tweet, user.get());
            return ResponseEntity.ok(updatedTweet);
        }
        else
            throw new UserNotFoundException("Requested User records does not exist.");
    }

    @DeleteMapping("/{username}/delete/{id}")
    @ApiOperation(value = "Delete a tweet posted by {username}",
            notes = "{username} => Username of the user who posted the tweet.\n\n{id} => Tweet ID of the tweet to be deleted",
            produces = "text/plain",
            response = ResponseEntity.class)
    public ResponseEntity<String> deleteTweetByUsername(@PathVariable("username") String username, @PathVariable("id") String id) throws TweetNotFoundException, UserNotFoundException {
        Optional<User> user = userService.findUserByUsername(username);
        if(user.isPresent()) {
            String success = tweetService.deleteTweet(id, user.get());
            return ResponseEntity.ok(success);
        }
        else
            throw new UserNotFoundException("Requested User records does not exist.");    }

    @PostMapping("/{username}/reply/{id}")
    @ApiOperation(value="Reply to a tweet",
            notes = "Request body should contain only **message** and **listOfTags** only.\n\n{username} => Username of the user replying to the tweet {id}\n\n{id} => Tweet ID of the original tweet, for which the reply is being posted.",
            consumes = "application/json",
            produces = "application/json",
            response = ResponseEntity.class)
    public ResponseEntity<Tweet> replyTweetByUsername(@RequestBody Tweet tweet, @PathVariable("username") String username, @PathVariable("id") String id) throws InvalidTweetMessageException, UserNotFoundException {
        Optional<User> user = userService.findUserByUsername(username);
        if(user.isPresent()) {
            tweet.setTweetedAt(LocalDateTime.now());
            Tweet tweetedReply = tweetService.replyTweet(id, tweet, user.get());
            return ResponseEntity.ok(tweetedReply);
        }
        else
            throw new UserNotFoundException("Requested User records does not exist.");
    }

    @PutMapping("/{username}/like/{id}")
    @ApiOperation(value = "Like a tweet",
            notes = "{username} => Username of the user liking the tweet.\n\n{id} => Tweet of the tweet which is being liked.",
            produces = "application/json",
            response = ResponseEntity.class)
    public ResponseEntity<Tweet> likeTweetByUsername(@PathVariable("username") String username, @PathVariable("id") String id) throws UserNotFoundException, InvalidTweetMessageException {
        Optional<User> user = userService.findUserByUsername(username);
        if(user.isPresent()) {
            Tweet updatedTweet = tweetService.updateListOfLikes(id, user.get());
            return ResponseEntity.ok(updatedTweet);
        }
        else
            throw new UserNotFoundException("Requested User records does not exist.");
    }

    @GetMapping("user/search/{username}")
    @ApiOperation(value = "Search for users",
            notes = "{username} => Partial or Complete starting of the Username",
            produces = "application/json",
            response = ResponseEntity.class)
    public ResponseEntity<List<User>> searchUsersByUsernameStartingWith(@PathVariable("username") String username) {
        List<User> users = userService.searchUsersByUsername(username);
        return ResponseEntity.ok(users);
    }

}
