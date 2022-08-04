package com.tweetapp.auth.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import com.tweetapp.auth.model.User;
import com.tweetapp.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService implements UserDetailsService{

	@Autowired
	private UserRepository userRepository;
	
	private final PasswordEncoder passwordEncoder;
	
	public User registerUser(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));

		User registeredUser = userRepository.insert(user);
		return registeredUser;
	}
	
	public Optional<User> getUserByUsername(String username) {
		Optional<User> user = userRepository.findUserByEmail(username);
		return user;
		
	}
	
	public Optional<User> findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }
	
	 public Optional<User> findUserByUsername(String username) {
	    return userRepository.findUserByUsername(username);
    }
	 
	 public boolean checkAnswerValidity(String email, String answer) throws UsernameNotFoundException {
        Optional<User> optionalUser = this.userRepository.findUserByEmail(email);
        if(optionalUser.isEmpty()) {
            throw new UsernameNotFoundException(answer);
        }
        if(optionalUser.get().getSecurityAnswer().equals(answer)) {
            return true;
        }
        return false;
    }
 
	 public User updatePassword(User user) throws UsernameNotFoundException {
	     Optional<User> optionalUser = this.userRepository.findUserByEmail(user.getEmail());
	     if(optionalUser.isEmpty()) {
	         throw new UsernameNotFoundException("Requested User records does not exist.");
	     }
	     User originalUser = optionalUser.get();
	     String password = user.getPassword();
	     originalUser.setPassword(passwordEncoder.encode(password));
	     this.userRepository.save(originalUser);
	     return originalUser;
	 }



	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		Optional<User> opUser = userRepository.findUserByEmail(email);
		if (opUser.isEmpty()) {
			System.out.println("user not found");
			throw new UsernameNotFoundException("Requested User records does not exist.");
		}
		else {
			System.out.println("User found");
		}
		User user = opUser.get();
		
		Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority("user"));
		
		return new org.springframework.security.core.userdetails.User(email, user.getPassword(), authorities);
	}
}
