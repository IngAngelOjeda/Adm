package py.gov.mitic.adminpy.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import java.io.Serializable;
import org.springframework.stereotype.Component;

@Component
public class PermissionChecker implements PermissionEvaluator {

    private Log logger = LogFactory.getLog(PermissionChecker.class);

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if ((authentication == null) || !(permission instanceof String)) {
            return false;
        }
        String targetType = targetDomainObject != null ? targetDomainObject.getClass().getSimpleName().toUpperCase() : "";
        logger.info("hasPermission: "+permission + " - targetType: "+targetType);
        return hasPrivilege(authentication, permission.toString());
    }

    @Override
    public boolean hasPermission(Authentication auth, Serializable targetId, String targetType, Object permission) {
        if ((auth == null) || (targetType == null) || !(permission instanceof String)) {
            return false;
        }
        logger.info("hasPermission: "+targetType+" - "+permission);
        return hasPrivilege(auth, permission.toString());
    }

    private boolean hasPrivilege(Authentication auth, String permission) {
        if(auth != null && auth.getAuthorities().size() > 0) {
            for (GrantedAuthority grantedAuth : auth.getAuthorities()) {
                // if (grantedAuth.getAuthority().startsWith(targetType) && grantedAuth.getAuthority().contains(permission)) {
                logger.info("grantedAuth.getAuthority(): "+grantedAuth.getAuthority()+" - "+permission);
                if (grantedAuth.getAuthority().contains(permission)) {
                    return true;
                }
            }
        }
        return false;
    }
}