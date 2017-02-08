package org.auslides.security.shiro;

import org.apache.shiro.authc.AuthenticationToken;

public class BearerToken implements AuthenticationToken {

	private static final long serialVersionUID = -8959620526701217063L;

	private final String email;
	private final String token;

	public BearerToken(String email, String token) {
		this.email = email;
		this.token = token;
	}

	@Override
	public Object getCredentials() {
		return token;
	}

	@Override
	public Object getPrincipal() {
		return email;
	}

}
