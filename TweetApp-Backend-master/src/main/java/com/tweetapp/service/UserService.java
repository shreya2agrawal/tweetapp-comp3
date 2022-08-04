package com.tweetapp.service;

import com.tweetapp.exception.InvalidUserCredentialsException;
import com.tweetapp.exception.UserNotFoundException;
import com.tweetapp.model.User;
import com.tweetapp.model.UserCredentials;
import com.tweetapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    ConsoleAppService consoleAppService;

    @Autowired
    UserRepository userRepository;

    public List<User> getListOfAllUsers() {
        return userRepository.findAll();
    }

    public User registerUser(User user){
        user.setCreatedAt(LocalDateTime.now());
        User registeredUser = userRepository.insert(user);
        System.out.println("Congratulations!!! You are now registered \n");
        return registeredUser;
    }

    public boolean checkAnswerValidity(String email, String answer) throws UserNotFoundException {
        Optional<User> optionalUser = this.userRepository.findUserByEmail(email);
        if(optionalUser.isEmpty()) {
            throw new UserNotFoundException("This user does not exist.");
        }
        if(optionalUser.get().getSecurityAnswer().equals(answer)) {
            return true;
        }
        return false;
    }

    public User updatePassword(User user) throws UserNotFoundException {
        Optional<User> optionalUser = this.userRepository.findUserByEmail(user.getEmail());
        if(optionalUser.isEmpty()) {
            throw new UserNotFoundException("This user does not exist.");
        }
        User originalUser = optionalUser.get();
        originalUser.setPassword(user.getPassword());
        this.userRepository.save(originalUser);
        return originalUser;
    }

    public UserCredentials loginUser(String email, String password) throws InvalidUserCredentialsException {
        Optional<User> optionalUser = userRepository.findUserByEmail(email);
        UserCredentials userDetails;
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            if(user.getPassword().equals(password)) {
                userDetails = new UserCredentials(email, null, user.getUsername());
                System.out.println("You have logged in successfully !!!!\n");
                return userDetails;
            } else {
                throw new InvalidUserCredentialsException("Requested User records does not exist.");

            }
        } else {
            throw new InvalidUserCredentialsException("Requested User records does not exist.");
        }
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    public Optional<User> findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    public List<User> searchUsersByUsername(String username) {
        List<User> userList = userRepository.findByUsernameLike(username);
        return userList;

    }

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
				Optional<User> opUser =  userRepository.findUserByEmail(email);
				if (opUser.isEmpty()) {
					System.out.println("user does not exist");
					throw new UsernameNotFoundException("incorrect credentials");
				}
				else {
					System.out.println("user found");
				}
				
				User user = opUser.get();
				Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
				authorities.add(new SimpleGrantedAuthority("user"));
				
				return new org.springframework.security.core.userdetails.User(email, user.getPassword(), authorities);
				
	}
}
