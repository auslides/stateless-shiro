package org.auslides.security.shiro.realm;

import org.auslides.security.model.DBAuthenticationToken;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;

class BearerAuthenticationInfo implements AuthenticationInfo {
    private static final long serialVersionUID = 3470761774414912759L;
    private BearerTokenAuthenticatingRealm bearerTokenAuthenticatingRealm;
    private final DBAuthenticationToken token;

    BearerAuthenticationInfo(BearerTokenAuthenticatingRealm bearerTokenAuthenticatingRealm, DBAuthenticationToken token) {
        this.bearerTokenAuthenticatingRealm = bearerTokenAuthenticatingRealm;
        this.token = token;
    }

    @Override
    public Object getCredentials() {
        return token.getToken();
    }

    @Override
    public PrincipalCollection getPrincipals() {
        RealmSecurityManager manager = (RealmSecurityManager) SecurityUtils.getSecurityManager();
        SimplePrincipalCollection ret = new SimplePrincipalCollection();
        for (Realm realm : manager.getRealms()) {
            /*
            if (realm instanceof ProfileRealm) {
                String email = token.getEmail();
                if (((ProfileRealm) realm).accountExists(email)) {
                    ret.add(email, realm.getName());
                }
            }
            */
        }
        ret.add(token.getEmail(), bearerTokenAuthenticatingRealm.getName());
        return ret;
    }

}
