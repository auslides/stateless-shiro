package org.auslides.security.model;

import java.util.logging.Logger;

public class DBAuthenticationToken {

	static final Logger LOGGER = Logger.getLogger(DBAuthenticationToken.class.getName());
	private int id;
	private String email; // not a Ref<DBMember> to support super-admins
	private String token;

	protected DBAuthenticationToken() {}

	public DBAuthenticationToken(String email, String token) {
		this.email = email;
		this.token = token;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
