package com.tweetapp.auth.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

import com.tweetapp.auth.exception.InvalidUserCredentialsException;
import com.tweetapp.auth.model.User;
import com.tweetapp.auth.model.UserCredentials;
import com.tweetapp.auth.util.LoginUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tweetapp.auth.service.UserService;


@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/v1.0/tweets")

public class UserController {
	
	@Autowired
	UserService userService;
	
	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	LoginUtil loginUtil;

	@Autowired
	private QueueMessagingTemplate queueMessagingTemplate;

	@Value("${cloud.aws.end-point.uri}")
	public String endpoint;
	
	@PostMapping("/register")
	@ApiOperation(value = "Register a new user",
			notes="User id and creation time will be auto-generated.\n\nPlease provide firstName, lastName, email, username, password, gender, dateOfBirth, securityAnswer and avatarLink attributes as part of request body.",
			consumes = "application/json",
			produces = "text/plain, application/json",
			response = ResponseEntity.class)
	public ResponseEntity<User> registerUser(@RequestBody User user) {
		URI uri = URI.create("/tweet-app:8080");
		user.setCreatedAt(LocalDateTime.now());
		User registeredUser = userService.registerUser(user);
		return ResponseEntity.created(uri).body(registeredUser);
	}
	
	@PostMapping("/login")
	@ApiOperation(value = "Validate User Credentials for login",
			consumes = "application/json",
			produces = "application/json",
			response = ResponseEntity.class)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody UserCredentials creds) throws InvalidUserCredentialsException {
		try {
			authenticationManager.authenticate(loginUtil.getAuthenticationToken(creds));
		}
		catch(BadCredentialsException e) {
			throw new InvalidUserCredentialsException("Credentials do not match our records. Please try again.");
		}
		
		UserDetails userDetails = userService.loadUserByUsername(creds.getEmail());
		HashMap<String,String> map = loginUtil.getTokens(userDetails);
		UserCredentials userCreds = new UserCredentials();
		Optional<User> opUser = userService.findUserByEmail(userDetails.getUsername());
		userCreds.setEmail(userDetails.getUsername());
		userCreds.setUsername(opUser.get().getUsername());
		userCreds.setAccess_token(map.get("access_token"));
		userCreds.setRefresh_token(map.get("refresh_token"));

		try {
			queueMessagingTemplate.send(endpoint, MessageBuilder.withPayload(userCreds.toString()).build());
		}catch(Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok(userCreds);
		
	}
	
	  @PostMapping("/auth/validate/question")
	  @ApiOperation(value = "Validate User Credentials for Security Questions",
			  notes = "Provide email and securityAnswer fields in request body",
			  consumes = "application/json",
			  produces = "text/plain",
			  response = ResponseEntity.class)
	    public boolean validateSecurityAnswer(@RequestBody User securityQuestionDetails) {
	        return userService.checkAnswerValidity(securityQuestionDetails.getEmail(), securityQuestionDetails.getSecurityAnswer());
	    }

    @PostMapping("/auth/changePassword")
	@ApiOperation(value = "Update user's password",
			notes = "Provide email and password fields in request body",
			consumes = "application/json",
			produces = "text/plain",
			response = ResponseEntity.class)
    public ResponseEntity<User> changePassword(@RequestBody User user) {
        User updatedUser = userService.updatePassword(user);
        return ResponseEntity.ok(updatedUser);
    }

}
