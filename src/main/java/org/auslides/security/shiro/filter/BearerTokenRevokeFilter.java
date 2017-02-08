package org.auslides.security.shiro.filter;

import org.auslides.security.shiro.util.HTTP;
import org.auslides.security.shiro.util.MessageBean;
import org.auslides.security.shiro.util.Messages;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.PathMatchingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class BearerTokenRevokeFilter extends PathMatchingFilter {
	private static final Logger LOGGER = LoggerFactory.getLogger(BearerTokenRevokeFilter.class.getName());

	private String logoutUrl = "/logout.jsp" ;

	public BearerTokenRevokeFilter() {
		super();
	}

	public void setLogoutUrl(String logoutUrl) {
		this.appliedPaths.put(logoutUrl, null);
	}

	@Override
	protected boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
		Subject subject = SecurityUtils.getSubject();
		try {
			subject.logout();
		} catch (SessionException ise) {
			LOGGER.info("Encountered session exception during logout.  This can generally safely be ignored.", ise);
		}
		HTTP.writeAsJSON(response,
				MessageBean.STATUS, HTTP.Status.OK.toInt(),
				MessageBean.MESSAGE, Messages.Status.OK.toString()) ;
		return false;
	}

}
