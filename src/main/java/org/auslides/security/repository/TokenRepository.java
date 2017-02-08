package org.auslides.security.repository;

import org.auslides.security.model.DBAuthenticationToken;

import java.util.Collection;

public interface TokenRepository {
    public static final String SECURET = "secret" ;

    String createAuthenticationToken(String email) ;
    DBAuthenticationToken getAuthenticationToken(String token) ;
    void deleteAuthenticationToken(String token);
    void deleteAuthenticationTokens(Collection<String> tokens);
    void deleteAll() ;
}
