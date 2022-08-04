package com.tweetapp.tweet.service;

import com.tweetapp.exception.InvalidTweetMessageException;
import com.tweetapp.exception.TweetNotFoundException;
import com.tweetapp.model.Gender;
import com.tweetapp.model.Tweet;
import com.tweetapp.model.User;
import com.tweetapp.repository.TweetRepository;
import com.tweetapp.service.TweetService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class TweetServiceTest
{

    @InjectMocks
    TweetService tweetService;

    @Mock
    TweetRepository tweetRepository;


    private Tweet getDummyTweet(String message, String userId) {
    	Tweet t = new Tweet();
    	t.setUserId(userId);
    	t.setMessage(message);
    	t.setListOfLikes(new HashMap<String,Boolean>());
    	t.setListOfTags(new ArrayList<String>());
        t.setUsername("username");
        return t;
    }
    private Tweet getDummyTweet(String userId) {
        Tweet tweet = new Tweet();
        tweet.setMessage("message");
        tweet.setUsername("username");
    	tweet.setUserId(userId);
    	tweet.setListOfLikes(new HashMap<String,Boolean>());
    	tweet.setListOfTags(new ArrayList<String>());
        return tweet;
    }
    private Tweet getDummyTweet() {
        Tweet tweet = new Tweet();
        tweet.setMessage("message");
    	tweet.setListOfLikes(new HashMap<String,Boolean>());
    	tweet.setListOfTags(new ArrayList<String>());
        return tweet;
    }
    private Tweet getDummyTweetWithUserDetails() {
        Tweet tweet = new Tweet();
    	tweet.setListOfLikes(new HashMap<String,Boolean>());
    	tweet.setListOfTags(new ArrayList<String>());
        tweet.setUsername("username");
        tweet.setUserId("1");
        tweet.setMessage("message");
        tweet.setTweetedAt(LocalDateTime.now());
        tweet.setNumberOfReplies(0);
        return tweet;
    }

    private List<Tweet> getAllTweets(String userId)
    {
        List<Tweet> list = new ArrayList<>();
        if (userId.trim().equals("")){
           list.add((getDummyTweet("1")));
           list.add((getDummyTweet("some message from user 1","1")));
           list.add((getDummyTweet("2")));
           return list;
        }
        else {
            list.add(getDummyTweet(userId));
        }
        return list;
    }

    private Tweet getDbAddedTweet(Tweet tweet) {
        tweet.setTweetedAt(LocalDateTime.now());
        tweet.setId("someRandomId");
        return tweet;
    }
    private Tweet getDbAddedTweet(Tweet tweet,String id) {
        tweet.setId(id);
        return tweet;
    }


    private List<Tweet> getReplies(String id) {
        List<Tweet> tweets = new ArrayList<>();
        for (int i=0;i<3; i++ ){
            Tweet tweet = (getDbAddedTweet(getDummyTweet(),String.valueOf(i)));
            tweet.setRepliedTo(id);
            tweets.add(tweet);
        }
        return tweets;
    }
    private User getDummyUser(String userId) {
        User user = new User();
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setEmail("email@gmail.com");
        user.setGender(Gender.FEMALE);
        user.setDateOfBirth(new Date());
        user.setPassword("password");
        user.setCreatedAt(LocalDateTime.now());
        user.setUsername("username");
        user.setAvatarLink("avatarLink");
        user.setId(userId);
        return user;
    }

    private Optional<List<Tweet>> getOptionalListTweet(String userId) {
        return Optional.of(getAllTweets(userId));
    }

    @Test
    void shouldAllReturnAllTweets(){

        when(tweetRepository.findAll(Sort.by(Sort.Direction.DESC, "tweetedAt"))).thenReturn(getAllTweets(""));
        List<Tweet> tweets = tweetService.getListOfAllTweets();

        assertThat(tweets).isNotNull().isNotEmpty().hasSize(3);
        assertThat(tweets.get(1))
                .isNotNull()
                .hasFieldOrPropertyWithValue("message","some message from user 1");
    }
    @Test
    void shouldAllReturnAllTweetsOfUser(){
        when(tweetRepository.findTweetsByUserId("1")).thenReturn(getOptionalListTweet("1"));
        List<Tweet> tweets = tweetService.findAllTweetsByUserId("1");

        assertThat(tweets).isNotNull().isNotEmpty().hasSize(1);
        assertThat(tweets.get(0))
                .isNotNull()
                .hasFieldOrPropertyWithValue("message","message")
                .hasFieldOrPropertyWithValue("userId","1");
    }

    @Test
    void shouldAddTweetForUser() {
        Tweet tweetWithUserDetails = getDummyTweetWithUserDetails();
        Tweet dbAddedTweet = getDbAddedTweet(getDummyTweetWithUserDetails());
        Tweet noUserDetailsTweet = getDummyTweet();
        User user = getDummyUser("1");

        tweetWithUserDetails.setTweetedAt(LocalDateTime.now());
        dbAddedTweet.setTweetedAt(tweetWithUserDetails.getTweetedAt());
        noUserDetailsTweet.setTweetedAt(tweetWithUserDetails.getTweetedAt());

//        doReturn(dbAddedTweet).when(tweetRepository.insert(tweet));
        when(tweetRepository.insert(tweetWithUserDetails)).thenReturn(dbAddedTweet);
        try {
        	Tweet addedTweet = tweetService.postANewTweet(noUserDetailsTweet,user);
        }
        catch(InvalidTweetMessageException e) {
        	
        }

        assertThat(dbAddedTweet)
                .isNotNull()
                .hasFieldOrPropertyWithValue("userId","1")
                .hasFieldOrPropertyWithValue("message","message");
    }


    @Test
    void shouldGetTweetForGivedTweetId() {
        Tweet dbAddedTweet = getDbAddedTweet(getDummyTweetWithUserDetails());
        when(tweetRepository.findById("someRandomId")).thenReturn(Optional.of(dbAddedTweet));

        Tweet tweet = tweetService.getTweetByTweetId("someRandomId");
        assertThat(tweet)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id","someRandomId")
                .hasFieldOrPropertyWithValue("message","message")
                .hasFieldOrPropertyWithValue("username","username");
    }

    @Test
    void shouldGetAllTweetForGivenReplidToId() {
        Tweet dbAddedTweet = getDbAddedTweet(getDummyTweetWithUserDetails());
        when(tweetRepository.findTweetsByRepliedTo("someRandomId")).thenReturn(Optional.of(getReplies("someRandomId")));

        List<Tweet> tweets = tweetService.getListOfAllReplies("someRandomId");
        assertThat(tweets)
                .isNotNull()
                .hasSize(3);
        assertThat(tweets.get(0)).hasFieldOrPropertyWithValue("repliedTo","someRandomId");
        assertThat(tweets.get(1)).hasFieldOrPropertyWithValue("repliedTo","someRandomId");
        assertThat(tweets.get(2)).hasFieldOrPropertyWithValue("repliedTo","someRandomId");

    }

    @Test
    void shouldUpdateTweetForGivenTweetId() {
        Tweet dbAddedTweet = getDbAddedTweet(getDummyTweetWithUserDetails());
        User user = getDummyUser("1");
        assertThat(dbAddedTweet).hasFieldOrPropertyWithValue("message","message");

        when(tweetRepository.findTweetByIdAndUser(dbAddedTweet.getId(),user.getId())).thenReturn(dbAddedTweet);
        Tweet incomingUpdatedTweet = getDbAddedTweet(getDummyTweetWithUserDetails());
        incomingUpdatedTweet.setMessage("updated message");
        dbAddedTweet.setListOfTags(incomingUpdatedTweet.getListOfTags());
        dbAddedTweet.setMessage(incomingUpdatedTweet.getMessage());

        Tweet updateTweet = getDbAddedTweet(getDummyTweet());
        updateTweet.setMessage(incomingUpdatedTweet.getMessage());
        when(tweetRepository.save(dbAddedTweet)).thenReturn(updateTweet);
        try {
            Tweet tweet = tweetService.updateTweet(incomingUpdatedTweet, getDummyUser("1"));
            assertThat(tweet)
                    .isNotNull()
                    .hasFieldOrPropertyWithValue("message","updated message");
        }
        catch (InvalidTweetMessageException |TweetNotFoundException t) {

        }
    }

    @Test
    void shouldReplyToGivenTweetId() {
        Tweet dbAddedTweet = getDbAddedTweet(getDummyTweetWithUserDetails());
        Tweet  replyTweet= getDummyTweet("reply message","2");
        replyTweet.setRepliedTo("someRandomId");

        Tweet dbAddedreplyTweet = getDbAddedTweet(replyTweet,"replyId");
        dbAddedreplyTweet.setRepliedTo("someRandomId");


        User user = getDummyUser("1");
        User user2 = getDummyUser("2");

        dbAddedTweet.setTweetedAt(LocalDateTime.now());

//        doReturn(dbAddedTweet).when(tweetRepository.insert(tweet));
        when(tweetRepository.findById("someRandomId")).thenReturn(Optional.of(dbAddedTweet));

        // set dte
        replyTweet.setTweetedAt(LocalDateTime.now());
        dbAddedreplyTweet.setTweetedAt(replyTweet.getTweetedAt());

        when(tweetRepository.insert((replyTweet))).thenReturn(dbAddedreplyTweet);
        dbAddedTweet.setNumberOfReplies(dbAddedTweet.getNumberOfReplies()+1);
        when(tweetRepository.save(dbAddedTweet)).thenReturn(dbAddedTweet);
        try {
            Tweet addedTweet = tweetService.replyTweet("someRandomId",replyTweet,user2);
            assertThat(addedTweet)
                    .isNotNull()
                    .hasFieldOrPropertyWithValue("userId","2")
                    .hasFieldOrPropertyWithValue("message","reply message");
        }
        catch(InvalidTweetMessageException e) {

        }

    }
}
