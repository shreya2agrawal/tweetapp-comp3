package com.tweetapp.auth.util;

import java.util.Date;
import java.util.HashMap;
import java.util.stream.Collectors;

import com.tweetapp.auth.model.UserCredentials;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

@Service
public class LoginUtil {
	
	public UsernamePasswordAuthenticationToken getAuthenticationToken(UserCredentials creds) {
		String username = creds.getEmail();
		String password = creds.getPassword();
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
		return authenticationToken;
	}
	
	public HashMap<String,String> getTokens(UserDetails user) {
		Algorithm algorithm = Algorithm.HMAC256("SECRET".getBytes());
		String access_token = JWT.create().withSubject(user.getUsername())
				.withExpiresAt(new Date(System.currentTimeMillis() + 6 *60 *60 * 1000))
				.withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
				.sign(algorithm);
		
		String refresh_token = JWT.create().withSubject(user.getUsername())
				.withExpiresAt(new Date(System.currentTimeMillis() + 120 *60 *60* 1000))
				.sign(algorithm);
		
//		response.setHeader("access_token", access_token);
//		response.setHeader("refresh_token", refresh_token);
		
		HashMap<String, String> tokens = new HashMap<>();
		tokens.put("access_token", access_token);
		tokens.put("refresh_token", refresh_token);
		
		return tokens;
	}

}
