package org.auslides.security.shiro.filter;

import org.auslides.security.repository.TokenRepository;
import org.auslides.security.shiro.BearerToken;
import org.auslides.security.shiro.util.HTTP;
import org.auslides.security.shiro.util.MessageBean;
import org.auslides.security.shiro.util.Messages;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

public final class BearerTokenAuthenticatingFilter extends AuthenticatingFilter {

	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String AUTHORIZATION_PARAM = "auth";
	private static final String AUTHORIZATION_SCHEME = "Bearer";
	private static final String AUTHORIZATION_SCHEME_ALT = "Basic";

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(BearerTokenAuthenticatingFilter.class.getName());

	@Autowired
    TokenRepository tokenRepository ;

	private String usernameParam;
	private String passwordParam;

	public void setUsernameParam(String usernameParam) {
		this.usernameParam = usernameParam;
	}

	public void setPasswordParam(String passwordParam) {
		this.passwordParam = passwordParam;
	}

	String getUsernameParam() {
		return usernameParam;
	}

	String getPasswordParam() {
		return passwordParam;
	}

	@Override
	protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {
		if (isLoginRequest(request, response)) {
			String JSON = request.getReader().readLine();
			JSONObject contents = new JSONObject(JSON);
			String username = (String) contents.get(getUsernameParam());
			String password = (String) contents.get(getPasswordParam());
			return createToken(username, password, request, response);
		} else {
			String authorizeHeader = getAuthorizationHeader(request);
			String authorizeParameter = getAuthorizationParameter(request);
			String[] principlesAndCredentials;

			if (isHeaderLoginAttempt(authorizeHeader)) {
				principlesAndCredentials = this.getHeaderPrincipalsAndCredentials(authorizeHeader);
			} else if (isParameterLoginAttempt(authorizeParameter)) {
				principlesAndCredentials = this.getParameterPrincipalsAndCredentials(authorizeParameter);
			} else {
				return null;
			}

			if (principlesAndCredentials == null || principlesAndCredentials.length != 2) {
				return null;
			}

			String username = principlesAndCredentials[0];
			String token = principlesAndCredentials[1];
			return new BearerToken(username, token);
		}
	}

	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		boolean authHasToken = hasAuthorizationToken(request);
		boolean isLogin = isLoginRequest(request, response);
		if (authHasToken || isLogin) {
			if (isLoginSubmission(request, response) || authHasToken) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Login submission detected.  Attempting to execute login.");
				}
				return executeLogin(request, response);
			} else {
				HTTP.writeError(response, HTTP.Status.UNAUTHORIZED);
				return false;
			}
		} else {
			HTTP.writeError(response, HTTP.Status.UNAUTHORIZED);
			return false;
		}
	}

	@Override
	protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) throws Exception {
		if (isLoginRequest(request, response)) {
			String email = (String)subject.getPrincipal();
			String newToken = tokenRepository.createAuthenticationToken(email);
			HTTP.writeAsJSON(response,
					MessageBean.STATUS, HTTP.Status.OK.toInt(),
					MessageBean.MESSAGE, Messages.Status.OK.toString(),
					MessageBean.TOKEN, newToken,
					MessageBean.EMAIL, email);
			return false;
		} else {
			return true;
		}
	}

	@Override
	protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
		if (isLoginRequest(request, response)) {
			HTTP.writeAsJSON(response,
					MessageBean.STATUS, HTTP.Status.UNAUTHORIZED.toInt(),
					MessageBean.MESSAGE, Messages.Status.UNAUTHORIZED.toString(),
					MessageBean.FAILURE_REASON, Messages.Login.getMessage(e));
		} else {
			HTTP.writeError(response, HTTP.Status.UNAUTHORIZED);
		}
		return false;
	}

	protected boolean isLoginSubmission(ServletRequest request, ServletResponse response) {
		return (request instanceof HttpServletRequest) && WebUtils.toHttp(request).getMethod().equalsIgnoreCase(POST_METHOD);
	}

/* TODO
	@Override
	public boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
		boolean b1 = (isLoginRequest(request, response) && hasAuthorizationToken(request)) ;
		boolean b2 = super.onPreHandle(request, response, mappedValue) ;
		LOGGER.info("***onPreHandle******:sLoginRequest:" + b1  + " super.onPreHandler:" + b2) ;
		return b1 || b2;
	}
*/
	boolean hasAuthorizationToken(ServletRequest request) {
		String authorizeHeader = getAuthorizationHeader(request);
		String authorizeParam = getAuthorizationParameter(request);
		return isHeaderLoginAttempt(authorizeHeader) || isParameterLoginAttempt(authorizeParam);
	}

	String getAuthorizationHeader(ServletRequest request) {
		HttpServletRequest httpRequest = WebUtils.toHttp(request);
		return httpRequest.getHeader(AUTHORIZATION_HEADER);
	}

	String getAuthorizationParameter(ServletRequest request) {
		HttpServletRequest httpRequest = WebUtils.toHttp(request);
		return WebUtils.getCleanParam(httpRequest, AUTHORIZATION_PARAM);
	}

	boolean isHeaderLoginAttempt(String authorizeHeader) {
		if (authorizeHeader == null) return false;
		String authorizeScheme = AUTHORIZATION_SCHEME.toLowerCase(Locale.ENGLISH);
		String authorizeSchemeAlt = AUTHORIZATION_SCHEME_ALT.toLowerCase(Locale.ENGLISH);
		String test = authorizeHeader.toLowerCase(Locale.ENGLISH);
		return test.startsWith(authorizeScheme) || test.startsWith(authorizeSchemeAlt);
	}

	boolean isParameterLoginAttempt(String authorizeParam) {
		return (authorizeParam != null) && Base64.isBase64(authorizeParam.getBytes());
	}

	String[] getHeaderPrincipalsAndCredentials(String authorizeHeader) {
		if (authorizeHeader == null) {
			return null;
		}
		String[] authTokens = authorizeHeader.split(" ");
		if (authTokens == null || authTokens.length < 2) {
			return null;
		}
		return getPrincipalsAndCredentials(authTokens[1]);
	}

	String[] getParameterPrincipalsAndCredentials(String authorizeParam) {
		if (authorizeParam == null) {
			return null;
		}
		return getPrincipalsAndCredentials(authorizeParam);
	}

	String[] getPrincipalsAndCredentials(String authorizeParam) {
		Jws<Claims> claims = Jwts.parser()
				.setSigningKey(TokenRepository.SECURET.getBytes())
				.parseClaimsJws(authorizeParam);
		String email = claims.getBody().getSubject();
		return new String[]{email, authorizeParam};
	}

	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
		return !(!isLoginRequest(request, response) && isPermissive(mappedValue) && hasAuthorizationToken(request)) && (super.isAccessAllowed(request, response, mappedValue) || (!isLoginRequest(request, response) && isPermissive(mappedValue) && !hasAuthorizationToken(request)));
	}

}
