package org.auslides.security.config;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.auslides.security.shiro.filter.BearerTokenAuthenticatingFilter;
import org.auslides.security.shiro.filter.BearerTokenRevokeFilter;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authc.credential.PasswordMatcher;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.auslides.security.shiro.realm.BearerTokenAuthenticatingRealm;
import org.auslides.security.shiro.realm.DatabaseRealm;
import org.auslides.security.shiro.stateless.StalessSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.servlet.Filter;
import java.util.*;

@Configuration
public class ShiroConfig {

    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean shiroFilter() {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager());

        // First define the alias in the ShiroFilterFactoryBean initialization
        Map<String, Filter> filters = new HashMap<>();
        filters.put("tokenAuthc", bearerTokenAuthenticatingFilter());
        filters.put("tokenLogout", bearerTokenRevokeFilter());
        shiroFilterFactoryBean.setFilters(filters);

        // then do the mappings using the alias
        Map<String, String> filterChainDefinitionMapping = new HashMap<>();
        filterChainDefinitionMapping.put("/users/auth", "noSessionCreation,tokenAuthc");
        filterChainDefinitionMapping.put("/users", "noSessionCreation,tokenAuthc");
        filterChainDefinitionMapping.put("/users/logout", "noSessionCreation,tokenAuthc,tokenLogout");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMapping);

        return shiroFilterFactoryBean;
    }

    /*
       Bearer token support
     */
    @Bean
    BearerTokenAuthenticatingFilter bearerTokenAuthenticatingFilter() {
        BearerTokenAuthenticatingFilter filter = new BearerTokenAuthenticatingFilter() ;
        filter.setUsernameParam("username") ;
        filter.setPasswordParam("password");
        filter.setLoginUrl("/users/auth");
        return filter ;
    }

    @Bean
    BearerTokenRevokeFilter bearerTokenRevokeFilter() {
        return new BearerTokenRevokeFilter() ;
    }

    /**
     * Don't touch the session anymore
     */
    @Bean
    public StalessSecurityManager securityManager()
    {
        StalessSecurityManager securityManager = new StalessSecurityManager() ;
        Collection<Realm> realms = Arrays.asList(bearerTokenAuthenticatingRealm(), databaseRealm()) ;
        securityManager.setRealms(realms);
        securityManager.setSessionManager(sessionManager());
        // DO NOT do this in web applications
        // https://shiro.apache.org/spring-boot.html#standalone-applications
        // SecurityUtils.setSecurityManager(securityManager) ;

        return securityManager;
    }

    @Bean
    public DefaultWebSessionManager sessionManager() {
        final DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setSessionValidationSchedulerEnabled(false);
        return sessionManager;
    }

    /*
       Realm, for user profile access
     */
    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public BearerTokenAuthenticatingRealm bearerTokenAuthenticatingRealm() {
        final BearerTokenAuthenticatingRealm realm = new BearerTokenAuthenticatingRealm();
        return realm;
    }

    // utility
    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DatabaseRealm databaseRealm() {
        final DatabaseRealm realm = new DatabaseRealm();
        realm.setCredentialsMatcher(credentialsMatcher());
        return realm;
    }

    @Bean(name = "credentialsMatcher")
    public PasswordMatcher credentialsMatcher() {
        final PasswordMatcher credentialsMatcher = new PasswordMatcher();
        credentialsMatcher.setPasswordService(passwordService());
        return credentialsMatcher;
    }

    @Bean(name = "passwordService")
    public DefaultPasswordService passwordService() {
        return new DefaultPasswordService();
    }

    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /*
     *  https://shiro.apache.org/spring.html#enabling-shiro-annotations
     *  use Shiro’s Annotations for security checks (for example, @RequiresRoles,
     *  @RequiresPermissions, etc) in controllers. This requires Shiro’s Spring
     *  AOP integration to scan for the appropriate annotated classes and perform
     *  security logic as necessary.
     *
     *  The following two bean definitions are for this purpose.
     *
     * Deprecated
     *
    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        defaultAdvisorAutoProxyCreator.setProxyTargetClass(true); // it's false by default
        return defaultAdvisorAutoProxyCreator;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(
            org.apache.shiro.mgt.SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }
    */

    /**
     * https://shiro.apache.org/spring.html#enabling-shiro-annotations
     */
    @Bean
    public ShiroFilterChainDefinition shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();
        chainDefinition.addPathDefinition("/**", "anon"); // all paths are managed via annotations

        // or allow basic authentication, but NOT require it.
        // chainDefinition.addPathDefinition("/**", "authcBasic[permissive]");
        return chainDefinition;
    }
}
