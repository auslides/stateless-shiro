package org.auslides.security.shiro.stateless;

import org.apache.shiro.session.Session;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.subject.support.DefaultSubjectContext;

/**
 * A subject context that extends the default, but does not have session support enabled.
 */
public class StalessSubjectContext extends DefaultSubjectContext {
    public StalessSubjectContext(SubjectContext ctx) {
        super(ctx);
    }

    public StalessSubjectContext() {
    }

    @Override
    public Session getSession() {
        return null ;
    }

    @Override
    public boolean isSessionCreationEnabled() {
         return false ;
    }
}
