package org.auslides.security.shiro.realm;

import org.auslides.security.model.Permission;
import org.auslides.security.model.Role;
import org.auslides.security.model.User;
import org.auslides.security.repository.UserRepository;
import org.auslides.security.shiro.util.HashHelper;
import com.google.common.base.Preconditions;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.util.SimpleByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedHashSet;
import java.util.Set;

public class DatabaseRealm extends AuthorizingRealm {
	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseRealm.class.getName());

	@Autowired
	private UserRepository userRepository;

	public DatabaseRealm() {
		super(HashHelper.getCredentialsMatcher());
		setAuthenticationTokenClass(UsernamePasswordToken.class);
        LOGGER.debug("Creating a new instance of DatabaseRealm");
	}

	public void clearCachedAuthorizationInfo(String principal) {
		SimplePrincipalCollection principals = new SimplePrincipalCollection(principal, getName());
		clearCachedAuthorizationInfo(principals);
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		Preconditions.checkNotNull(principals, "You can't have a null collection of principals. No really, how did you do that");
		String userEmail = (String) getAvailablePrincipal(principals);
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
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		if (token instanceof UsernamePasswordToken) {
			String userEmail = ((UsernamePasswordToken) token).getUsername();
			return doGetAuthenticationInfo(userEmail);
		}
		throw new UnsupportedOperationException("Implement another method of getting user email.");
	}

	private AuthenticationInfo doGetAuthenticationInfo(String email) throws AuthenticationException {
		Preconditions.checkNotNull(email, "Email can't be null");
        LOGGER.info("Finding authentication info for " + email + " in DB");

		final User user = userRepository.findByEmailAndActive(email, true);

		SimpleAccount account = new SimpleAccount(user.getEmail(), user.getPassword().toCharArray(),
                new SimpleByteSource(email), getName());

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

		account.setRoles(roleNames);
		account.setStringPermissions(permissionNames);
		return account;
	}
}