package org.auslides.security.repository;

import org.auslides.security.mapper.TokenMapper;
import org.auslides.security.model.DBAuthenticationToken;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TokenRepositoryImpl implements TokenRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenRepositoryImpl.class.getName());

    @Autowired
    TokenMapper tokenMapper ;

    private Map<String, String> cache = new HashMap<>() ;

    @Override
    public String createAuthenticationToken(String email) {
        LocalDate currentDate = LocalDate.now();
        LocalDate nextMoth = currentDate.plusMonths(1) ;
        Date date = Date.from(nextMoth.atStartOfDay(ZoneId.systemDefault()).toInstant());
        String jwtToken = Jwts.builder()
                .setSubject(email)
                .setExpiration(date)
                .signWith(
                        SignatureAlgorithm.HS256,
                        SECURET.getBytes()
               )
                .compact();

        DBAuthenticationToken dbAuthenticationToken = new DBAuthenticationToken(email, jwtToken) ;
        tokenMapper.insert(dbAuthenticationToken);
        return jwtToken;
    }

    @Override
    public DBAuthenticationToken getAuthenticationToken(String token) {
        DBAuthenticationToken dbAuthenticationToken = tokenMapper.getByToken(token) ;
        return dbAuthenticationToken ;
    }

    @Override
    public void deleteAuthenticationToken(String token) {
        tokenMapper.deleteByToken(token);
    }

    @Override
    public void deleteAuthenticationTokens(Collection<String> tokens) {
        tokens.stream().forEach(token->{
            deleteAuthenticationToken(token);
        });
    }

    @Override
    public void deleteAll() {
         tokenMapper.deleteAll();
    }
}
