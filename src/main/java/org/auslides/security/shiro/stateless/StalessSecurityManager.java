package org.auslides.security.shiro.stateless;

import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;

public class StalessSecurityManager extends DefaultWebSecurityManager {
    public StalessSecurityManager() {
        setSubjectFactory(new StalessSubjectFactory()) ;

        // Disabling Subject State Session Storage
        // https://shiro.apache.org/session-management.html#disabling-subject-state-session-storage
        DefaultSubjectDAO subjectDAO = (DefaultSubjectDAO)this.getSubjectDAO() ;
        DefaultSessionStorageEvaluator sessionStorageEvaluator = (DefaultSessionStorageEvaluator)subjectDAO.getSessionStorageEvaluator() ;
        sessionStorageEvaluator.setSessionStorageEnabled(false) ;
        this.setRememberMeManager(null) ;
    }

    @Override
    protected SubjectContext createSubjectContext() {
        return new StalessSubjectContext();
    }

    @Override
    protected SubjectContext copy(SubjectContext subjectContext ) {
        if (subjectContext instanceof StalessSubjectContext) {
            return new StalessSubjectContext(subjectContext) ;
        }
        return super.copy(subjectContext) ;
    }

}
