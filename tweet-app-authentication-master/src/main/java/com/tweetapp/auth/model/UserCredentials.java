package com.tweetapp.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCredentials {
	
	String email;
	String username;
	String password;
	String access_token;
	String refresh_token;
	
	public UserCredentials(String email, String password) {
		this.email = email;
		this.password = password;
	}

	@Override
	public String toString() {
		return "UserCredentials{" +
				"'email':'" + email  + "'" +
				", 'username:'" + username + "'"  +
				", 'password':'" + password + "'"  +
				", 'access_token':'" + access_token + "'"  +
				", 'refresh_token':'" + refresh_token + "'"  +
				'}';
	}
}
