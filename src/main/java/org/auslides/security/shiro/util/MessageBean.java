package org.auslides.security.shiro.util;

/**
 * This simple wrapper class allows us to return status messages from the Google App Engine Cloud Endpoints.
 */
public class MessageBean {


	public static final String STATUS = "status";
	public static final String MESSAGE = "message";
	public static final String FAILURE_REASON = "failureReason";

	public static final String TOKEN = "token";
	public static final String EMAIL = "email";

	public MessageBean(Integer status, String message) {
		this.status = status;
		this.message = message;
	}

	public MessageBean(Integer status, String message, String failureReason) {
		this.status = status;
		this.message = message;
		this.failureReason = failureReason;
	}

	public MessageBean(Integer status, String message, String email, String token) {
		this.status = status;
		this.message = message;
		this.email = email;
		this.token = token;
	}

	private Integer status;
	private String message;
	private String failureReason;
	private String email;
	private String token;

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getFailureReason() {
		return failureReason;
	}

	public void setFailureReason(String failureReason) {
		this.failureReason = failureReason;
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
