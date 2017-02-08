package org.auslides.security.config;

import org.auslides.security.repository.TokenRepository;
import org.auslides.security.repository.TokenRepositoryImpl;
import org.auslides.security.repository.UserRepository;
import org.auslides.security.repository.UserRepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfig {
    @Bean
    public UserRepository userRepository() {
        return new UserRepositoryImpl() ;
    }

    @Bean
    public TokenRepository tokenRepository() {
        return new TokenRepositoryImpl() ;
    }

}
