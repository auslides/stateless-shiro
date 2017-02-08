package org.auslides.security.shiro.util;

import org.apache.shiro.authc.*;

public final class Messages {

	private Messages() {}

	public enum Status {
		OK("ok"),
		FAILED("failed"),
		UNAUTHORIZED("unauthorized");

		private final String text;

		private Status(final String text) {
			this.text = text;
		}

		@Override
		public String toString() {
			return text;
		}

	}

	public enum Login {
		NO_SUCH_USER("noSuchUser"),
		BAD_PASSWORD("badPassword"),
		ACCOUNT_LOCKED("accountLocked"),
		ACCT_DISABLE("accountDisabled"),
		MANY_ATTEMPT("tooManyAttempts"),
		INVALID_DATA("invalidData");

		private final String text;

		private Login(final String text) {
			this.text = text;
		}

		@Override
		public String toString() {
			return text;
		}

		public static Messages.Login get(AuthenticationException ae) {
			if (ae instanceof UnknownAccountException) {
				return Messages.Login.NO_SUCH_USER;
			} else if (ae instanceof IncorrectCredentialsException) {
				return Messages.Login.BAD_PASSWORD;
			} else if (ae instanceof LockedAccountException) {
				return Messages.Login.ACCOUNT_LOCKED;
			} else if (ae instanceof DisabledAccountException) {
				return Messages.Login.ACCT_DISABLE;
			} else if (ae instanceof ExcessiveAttemptsException) {
				return Messages.Login.MANY_ATTEMPT;
			}
			return Messages.Login.INVALID_DATA;
		}

		public static String getMessage(AuthenticationException ae) {
			return get(ae).toString();
		}
	}

	public enum Register {
		ALREADY_USER("alreadyUser"),
		BAD_EMAIL_ADDRESS("badEmailAdd"),
		BAD_PASSWORD("badPassword"),
		PASSWORDS_MATCH("passwordMatch"),
		INVALID_PHONE("badPhoneNum"),
		INVALID_DATA("invalidData"),
		NO_SUCH_MEMBER("superForgot");

		private final String text;

		private Register(final String text) {
			this.text = text;
		}

		@Override
		public String toString() {
			return text;
		}
	}

	public enum Activate {
		CODE_EXPIRED("expiredCode"),
		NO_SUCH_USER("noSuchUser"),
		BAD_PASSWORD("badPassword"),
		PASSWORDS_MATCH("passwordMatch"),
		INVALID_DATA("invalidData");

		private final String text;

		private Activate(final String text) {
			this.text = text;
		}

		@Override
		public String toString() {
			return text;
		}
	}

}
