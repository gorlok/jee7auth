package com.sample.jee7auth;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;

import javax.annotation.Priority;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		// Get the HTTP Authorization header from the request
		String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

		// Check if the HTTP Authorization header is present and formatted
		// correctly
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			throw new NotAuthorizedException("Authorization header must be provided");
		}

		// Extract the token from the HTTP Authorization header
		String token = authorizationHeader.substring("Bearer".length()).trim();

		try {
			// Validate the token
			final Map<String, Object> claims = validateToken(token);
			final String username = (String) claims.get("sub");
//			final long expires = (Long) claims.get("exp");
//			if (expires > System.currentTimeMillis() / 1000L) {
//				throw new RuntimeException("jwt token expired");
//			}
			
			@SuppressWarnings("unchecked")
			final List<String> roles = (List<String>) claims.get("roles");

			requestContext.setSecurityContext(new SecurityContext() {
				@Override
				public Principal getUserPrincipal() {
					return new Principal() {
						@Override
						public String getName() {
							return username;
						}
					};
				}

				@Override
				public boolean isUserInRole(String role) {
					return (getRoles().contains(role));
				}

				@Override
				public boolean isSecure() {
					return false;
				}

				@Override
				public String getAuthenticationScheme() {
					return null;
				}
				
				public List<String> getRoles() {
					return roles;
				}
			});

		} catch (Exception e) {
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
		}
	}

	private Map<String, Object> validateToken(String token) throws Exception {
		// FIXME check is token was not revoked!
		/* if token was revoked
		 * 		throw new Exception('token was revoked');
		 */
		
		// Check if it was issued by the server and if it's not expired
		// Throw an Exception if the token is invalid
		return new TokenManager().validateToken(token);
	}
}