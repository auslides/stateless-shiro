package org.auslides.security.shiro.filter;

import org.auslides.security.shiro.util.MessageBean;
import org.auslides.security.shiro.util.Messages;
import com.google.common.collect.Maps;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;

public class WebAuthenticationFilter extends FormAuthenticationFilter {
	private static final Logger LOGGER = Logger.getLogger(WebAuthenticationFilter.class.getName());

	@Override
	protected void setFailureAttribute(ServletRequest request, AuthenticationException ae) {
		request.setAttribute(MessageBean.FAILURE_REASON, Messages.Login.getMessage(ae));
	}

	@Override
	protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
		AuthenticationToken token = createToken(request, response);
		Subject subject = SecurityUtils.getSubject();
		Session originalSession = subject.getSession();

		Map<Object, Object> attributes = Maps.newLinkedHashMap();
		Collection<Object> keys = originalSession.getAttributeKeys();
		for(Object key : keys) {
			Object value = originalSession.getAttribute(key);
			if (value != null) {
				attributes.put(key, value);
			}
		}
		originalSession.stop();

		try {
			subject.login(token);

			Session newSession = subject.getSession();
			for(Object key : attributes.keySet() ) {
				newSession.setAttribute(key, attributes.get(key));
			}

			LOGGER.fine("Creating a new instance of DatastoreRealm");

			return onLoginSuccess(token, subject, request, response);
		} catch (AuthenticationException e) {
			LOGGER.fine("Failed log in.");
			return onLoginFailure(token, e, request, response);
		}
	}

}
