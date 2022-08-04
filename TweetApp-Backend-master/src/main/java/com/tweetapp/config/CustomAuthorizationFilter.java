package com.tweetapp.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.tweetapp.model.User;
import com.tweetapp.service.UserService;


@Component
public class CustomAuthorizationFilter extends OncePerRequestFilter {
	
	@Autowired
	UserService userService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		if(request.getServletPath().equals("/api/v1.0/tweets/auth/validate/question")) {
			System.out.println("came in if");
//			response.setHeader("Access-Control-Allow-Origin", "*");
			filterChain.doFilter(request, response);
		}
		if(request.getServletPath().equals("/api/v1.0/tweets/auth/changePassword")) {
			System.out.println("came in if");
//			response.setHeader("Access-Control-Allow-Origin", "*");
			filterChain.doFilter(request, response);
		}
		else {
			String authorizationHeader = request.getHeader("Authorization");
			if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
				try {
					String token = authorizationHeader.substring("Bearer ".length());
					Algorithm algorithm = Algorithm.HMAC256("SECRET".getBytes());
					JWTVerifier verifier = JWT.require(algorithm).build();
					DecodedJWT decodedJWT = verifier.verify(token);
					String username = decodedJWT.getSubject();
					System.out.println("username = "+username);
					
					if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
						UserDetails userDetails = userService.loadUserByUsername(username);
						
						
							String roles[] = decodedJWT.getClaim("roles").asArray(String.class);
							Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
							
							Stream.of(roles).forEach(role -> {
								authorities.add(new SimpleGrantedAuthority(role));
							});
							
							UsernamePasswordAuthenticationToken upAuthToken = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
							upAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
							SecurityContextHolder.getContext().setAuthentication(upAuthToken);
							
						
					}
					else {
						System.out.println("username is not found, invalid token");

					}
					
				}catch(Exception e) {
					e.printStackTrace();
					System.out.println("Inavlid token");

				}
				
				filterChain.doFilter(request, response);
			}
			
		}
	}

}
