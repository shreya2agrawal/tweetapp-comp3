package com.tweetapp.tweet.service;

import com.tweetapp.model.Gender;
import com.tweetapp.model.User;
import com.tweetapp.model.UserCredentials;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.tweetapp.repository.UserRepository;
import com.tweetapp.service.UserService;

import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
	
	@InjectMocks
	private UserService userService;
	
	@Mock
	private UserRepository userRepository;

	private User getDummyUser() {
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
		return user;
	}

	private Optional<User> getOptionalUser() {
		Optional<User> optionalUser = Optional.of(getDummyUser());
		return optionalUser;
	}

	private Optional<User> getEmptyOptionalUser() {
		return Optional.empty();
	}
	
	private User registerUser(User user) {
		user.setId("someRandomId");
		return user;
	}

	private List<User> getListOfUser() {
		List<User> list = new ArrayList<>();
		list.add(registerUser(getDummyUser()));
		return list;
	}
	private UserCredentials getDummyUserCreds(User user) {
		UserCredentials creds = new UserCredentials();
		creds.setPassword(user.getPassword());
		creds.setId(user.getEmail());
		return creds;
	}

	@Test
	void shouldReturnAllUsers() {
		when(userRepository.findAll()).thenReturn(getListOfUser());
		List<User> userList = userService.getListOfAllUsers();

		assertThat(userList).hasSize(1).isNotNull().isNotEmpty();
		assertThat(userList.get(0)).hasFieldOrProperty("id").hasFieldOrPropertyWithValue("firstName","firstName");
	}
	
	@Test
	void shouldReturnUserWithProvideEmailId() {
		String email = "email@gmail.com";

		when(userRepository.findUserByEmail(email)).thenReturn(getOptionalUser());
		Optional<User> optionalUser = userService.findUserByEmail(email);

		assertThat(optionalUser.get()).isNotNull().hasFieldOrPropertyWithValue("firstName","firstName");

	}
	
	@Test
	void shouldNotReturnUserWithNonExistingEmailId() {
		String email = "email@gil.com";
		when(userRepository.findUserByEmail(email)).thenReturn(getEmptyOptionalUser());
		Optional<User> optionalUser = userService.findUserByEmail(email);

		assertThat(optionalUser).isEmpty();
	}
}
