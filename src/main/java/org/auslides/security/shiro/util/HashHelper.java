package org.auslides.security.shiro.util;

import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.SimpleByteSource;

public class HashHelper {

	private static final int HASH_ITERATIONS = 1;
	private static final String HASH_ALGORITHM = Sha256Hash.ALGORITHM_NAME;
	
	private static CredentialsMatcher credentials;
	private static RandomNumberGenerator salter;

	public static synchronized CredentialsMatcher getCredentialsMatcher() {
		if (credentials == null) {
			HashedCredentialsMatcher credentials = new HashedCredentialsMatcher(HASH_ALGORITHM);
			credentials.setHashIterations(HASH_ITERATIONS);
			credentials.setStoredCredentialsHexEncoded(true);
		}
		return credentials;
	}

	private static synchronized RandomNumberGenerator getSalter() {
		if (salter == null) {
			salter = new SecureRandomNumberGenerator();
		}
		return salter;
	}
	
	public static byte[] getSaltedBytes() {
		return getSalter().nextBytes().getBytes();
	}

	public static String getHashString(String secret, byte[] salt) {
		return (secret == null) ? null : new SimpleHash(HASH_ALGORITHM, secret, new SimpleByteSource(salt), HASH_ITERATIONS).toHex();
	}

}
