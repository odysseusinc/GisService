package org.ohdsi.gisservice.security;

import lombok.var;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class PermissionEvaluatorImpl implements PermissionEvaluator {

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {

        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {

        String requestedPermission = targetType + ":" + targetId + ":" + permission;
        List<String> grantedPermissions = extractPermissions(authentication);
        return grantedPermissions.stream().anyMatch(p -> matchesShiroPermission(p, requestedPermission));
    }

    private List<String> extractPermissions(Authentication authentication) {

        if (authentication.getDetails() instanceof List) {
            return (List) authentication.getDetails();
        }

        return new ArrayList<>();
    }

    private boolean matchesShiroPermission(String shiroPermission, String requestedPermission) {

        if (shiroPermission == null || requestedPermission == null) {
            return false;
        }

        if (Objects.equals(shiroPermission, requestedPermission)) {
            return true;
        }

        var shiroPermLevels = shiroPermission.split(":");
        var requestedPermLevels = requestedPermission.split(":");

        for (int i = 0; i < requestedPermLevels.length; i++) {
            if (!Objects.equals(shiroPermLevels[i], "*") && !Objects.equals(shiroPermLevels[i], requestedPermLevels[i])) {
                return false;
            }
        }

        return true;
    }
}
