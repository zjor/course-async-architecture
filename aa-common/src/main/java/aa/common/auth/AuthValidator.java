package aa.common.auth;

import aa.common.model.HasRole;
import aa.common.model.Role;

import java.util.Arrays;

public class AuthValidator {

    public static boolean hasRoles(HasRole account, Role... roles) {
        return Arrays.stream(roles).anyMatch(role -> account.getRole() == role);
    }

    public static boolean notOneOf(HasRole account, Role... roles) {
        return !hasRoles(account, roles);
    }

}
