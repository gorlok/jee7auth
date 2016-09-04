package com.sample.jee7auth;

import java.security.Principal;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Path("/")
public class MyEndpoint {

	@Context
	SecurityContext securityContext;

	@GET
	@Path("{id}")
	@Produces("application/json")
	public Response myUnsecuredMethod(@PathParam("id") Long id) {
		// This method is not annotated with @Secured
		// The authentication filter won't be executed before invoking this
		// method

		return Response.ok("Hello Guest - Id:" + id).build();
	}

	@DELETE
	@Secured
	@Path("{id}")
	@Produces("application/json")
	public Response mySecuredMethod(@PathParam("id") Long id) {
		// This method is annotated with @Secured
		// The authentication filter will be executed before invoking this
		// method
		// The HTTP request must be performed with a valid token

		Principal principal = securityContext.getUserPrincipal();
		String username = principal.getName();

		return Response.ok("Hello " + username + " - Id:" + id).build();
	}
}