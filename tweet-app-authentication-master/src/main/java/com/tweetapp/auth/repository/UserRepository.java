package com.tweetapp.auth.repository;

import java.util.Optional;

import com.tweetapp.auth.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
	
	
	Optional<User> findUserByEmail(String email);
	
	Optional<User> findUserByUsername(String username);
}
