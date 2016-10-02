package com.sample.jee7auth;

import org.mindrot.jbcrypt.BCrypt;

public class BCryptTest {

	public static void main(String[] args) {
		final String password = "s3cr37";
		
		// Hash a password for the first time
		String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
		System.out.println("BCrypt hash " + hashed);

		// gensalt's log_rounds parameter determines the complexity
		// the work factor is 2**log_rounds, and the default is 10
		//String hashed = BCrypt.hashpw(password, BCrypt.gensalt(12));

		final String candidate = "s3cr37";
		// Check that an unencrypted password matches one that has
		// previously been hashed
		if (BCrypt.checkpw(candidate, hashed))
			System.out.println("It matches");
		else
			System.out.println("It does not match");
	}

}
