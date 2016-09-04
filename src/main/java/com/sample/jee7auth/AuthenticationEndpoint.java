package com.sample.jee7auth;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

@Path("/authentication")
public class AuthenticationEndpoint {

	@POST
	@Produces("application/json")
	@Consumes("application/json")
	public Response authenticateUser(Credentials credentials) {
		String username = credentials.getUsername();
		String password = credentials.getPassword();

		try {
			// Authenticate the user using the credentials provided
			authenticate(username, password);
			
			// load roles FIXME
			List<String> roles = Arrays.asList("admin", "role1", "role2");
			
			// Issue a token for the user
			String token = issueToken(username, roles);

			// Return the token on the response
			//Authorization: Bearer <token-goes-here>
			
			return Response.ok(token).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).build();

		} catch (Exception e) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
	}

	private void authenticate(String username, String password) throws Exception {
		// Authenticate against a database, LDAP, file or whatever
		// Throw an Exception if the credentials are invalid
		
		// FIXME
		if (username.equals("admin") && password.equals("secret")) {
			// ok: user authenticated
		} else { 
			throw new Exception("invalid user name or password");
		}
	}

	private String issueToken(String username, List<String> roles) {
		// Issue a token (can be a random String persisted to a database or a
		// JWT token)
		// The issued token must be associated to a user
		// Return the issued token
		
		Map<String, Object> claims = new HashMap<>();
		claims.put("sub", username);
		claims.put("roles", roles);
		
		return new TokenManager().issueToken(claims);
	}

}