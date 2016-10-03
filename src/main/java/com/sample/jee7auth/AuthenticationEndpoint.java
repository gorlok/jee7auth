package com.sample.jee7auth;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

@Path("/auth")
public class AuthenticationEndpoint {

	@Inject
	TokenManager tokenManager;
	
	@POST
	@Produces("text/plain")
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
			
			NewCookie authCookie = new NewCookie("AUTH", token, "/", null, 1, null, -1, null, true, true);
			
			// Return the token on the response
			// Authorization: Bearer <token-goes-here>
			return Response.ok(token).header(HttpHeaders.AUTHORIZATION, "Bearer " + token).cookie(authCookie).build();

		} catch (Exception e) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
	}
	
	@POST
	@Secured
	@Path("/renew")
	@Produces("text/plain")
	public Response renewToken(@Context HttpHeaders headers) {
		try {
			// Get the HTTP Authorization header from the request
			String authorizationHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION);
			// Check if the HTTP Authorization header is present and formatted
			// correctly
			if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
				throw new NotAuthorizedException("Authorization header must be provided");
			}
			// Extract the token from the HTTP Authorization header
			String token = authorizationHeader.substring("Bearer".length()).trim();

			// load roles FIXME
			List<String> roles = Arrays.asList("admin", "role1", "role2");
			final Map<String, Object> claims = tokenManager.validateToken(token);
			
			
			final String username = (String) claims.get("sub");
			
			// Issue a token for the user
			String newToken = issueToken(username, roles);
			
			NewCookie authCookie = new NewCookie("AUTH", newToken, "/", null, 1, null, -1, null, true, true);
			
			// Return the token on the response
			// Authorization: Bearer <token-goes-here>
			return Response.ok(token).header(HttpHeaders.AUTHORIZATION, "Bearer " + newToken).cookie(authCookie).build();

		} catch (Exception e) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
	}

	private void authenticate(String username, String password) throws Exception {
		// Authenticate against a database, LDAP, file or whatever
		// Throw an Exception if the credentials are invalid
		
		// FIXME
		// Remember: store password's hashes, NEVER store passwords. Y can create salted hashes with BCrypt.
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