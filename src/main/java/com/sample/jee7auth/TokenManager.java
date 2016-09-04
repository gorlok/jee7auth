package com.sample.jee7auth;

import java.util.HashMap;
import java.util.Map;

import com.auth0.jwt.JWTSigner;
import com.auth0.jwt.JWTVerifier;

public class TokenManager {

	final String ISSUER = "https://mydomain.com/";
	final String SECRET = "secret"; // {{secret used for signing}}

	public String issueToken(final Map<String, Object> extraClaims) {
		// Issue a token (can be a random String persisted to a database or a
		// JWT token)
		// The issued token must be associated to a user
		// Return the issued token

		final long iat = System.currentTimeMillis() / 1000L; // issued at claim
		final long exp = iat + 60L; // expires claim. In this case the token
									// expires in 60 seconds

		final JWTSigner signer = new JWTSigner(SECRET);
		final HashMap<String, Object> claims = new HashMap<>();
		claims.put("iss", ISSUER);
		claims.put("exp", exp);
		claims.put("iat", iat);

		// add extra claims
		extraClaims.forEach((k,v) -> claims.put(k, v));

		final String jwt = signer.sign(claims);

		return jwt;
	}

	public Map<String, Object> validateToken(String jwtToken) throws Exception {
		try {
			final JWTVerifier verifier = new JWTVerifier(SECRET);
			final Map<String, Object> claims = verifier.verify(jwtToken);
			return claims;
		} catch (Exception ex) {
			throw new Exception("invalid tocken");
		}
	}

}
