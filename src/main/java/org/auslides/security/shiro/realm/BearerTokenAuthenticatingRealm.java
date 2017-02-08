package org.auslides.security.shiro.realm;

import org.auslides.security.model.Permission;
import org.auslides.security.model.Role;
import org.auslides.security.model.User;
import org.auslides.security.repository.TokenRepository;
import org.auslides.security.repository.UserRepository;
import org.auslides.security.shiro.BearerToken;
import org.auslides.security.model.DBAuthenticationToken;
import com.google.common.base.Preconditions;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.AllowAllCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class BearerTokenAuthenticatingRealm extends AuthorizingRealm {
	private static final Logger LOGGER = LoggerFactory.getLogger(BearerTokenAuthenticatingRealm.class.getName());

	@Autowired
    TokenRepository tokenRepository ;

	@Autowired
	private UserRepository userRepository;


	public BearerTokenAuthenticatingRealm() {
		super(new AllowAllCredentialsMatcher());
		setAuthenticationTokenClass(BearerToken.class);
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
		Preconditions.checkNotNull(principalCollection, "You can't have a null collection of principals. No really, how did you do that");
		String userEmail = (String) getAvailablePrincipal(principalCollection);
		if (userEmail == null) {
			throw new NullPointerException("Can't find a principal in the collection");
		}
		LOGGER.debug("Finding authorization info for " + userEmail + " in DB");

		final User user = userRepository.findByEmailAndActive(userEmail, true);

		LOGGER.debug("Found " + userEmail + " in DB");

		final int totalRoles = user.getRoles().size();
		final Set<String> roleNames = new LinkedHashSet<>(totalRoles);
		final Set<String> permissionNames = new LinkedHashSet<>();
		if (totalRoles > 0) {
			for (Role role : user.getRoles()) {
				roleNames.add(role.getName());
				for (Permission permission : role.getPermissions()) {
					permissionNames.add(permission.getName());
				}
			}
		}

		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		info.addRoles(roleNames);
		info.addStringPermissions(permissionNames);
		return info;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken arg0) throws AuthenticationException {
		BearerToken token = (BearerToken)arg0;
		// assert the bearerToken, and if valid, look up the account data and return
        //an AuthenticationInfo instance representing that account.
		String email = (String)token.getPrincipal();
		String credentials = (String)token.getCredentials();

		Preconditions.checkNotNull(email, "Email can't be null");
		Preconditions.checkNotNull(token, "Token can't be null");

		DBAuthenticationToken dbToken = tokenRepository.getAuthenticationToken(credentials) ;
		if (tokenIsInvalid(token, dbToken)) {
			LOGGER.info("Rejecting token " + credentials + " for user " + email);
			return null;
		}

		return new BearerAuthenticationInfo(this, dbToken);
	}

	@Override
	public void onLogout(PrincipalCollection principals) {
		super.onLogout(principals);
		deleteTokens(principals);
	}

	private static boolean tokenIsInvalid(BearerToken token, DBAuthenticationToken dbToken) {
		return token == null || dbToken == null || !dbToken.getEmail().equals(token.getPrincipal());
	}

	@SuppressWarnings("unchecked")
	private void deleteTokens(PrincipalCollection principals) {
		Collection<String> tokens = principals.fromRealm(getName());
		if (tokens != null) { //  && tokens.size() > 1
			tokenRepository.deleteAuthenticationTokens(tokens);
		}
	}
}
