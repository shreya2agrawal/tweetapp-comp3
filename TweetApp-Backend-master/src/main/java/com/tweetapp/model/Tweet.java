package com.tweetapp.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "tweet")
@ApiModel(description = "Details about the Tweet")
public class Tweet {

    @Id
    private String id;
    private String message;
    private String userId;
    private String username;
    private String repliedTo;
    private int numberOfReplies;
    private List<String> listOfTags;
    private HashMap<String, Boolean> listOfLikes;
    private LocalDateTime tweetedAt;

    @Override
    public String toString() {
        return "Tweet{" +
                "id='" + id + '\'' +
                ", message='" + message + '\'' +
                ", userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", repliedTo='" + repliedTo + '\'' +
                ", numberOfReplies=" + numberOfReplies +
                ", listOfTags=" + listOfTags +
                ", listOfLikes=" + listOfLikes +
                ", tweetedAt=" + tweetedAt +
                '}';
    }
}
