package aa.auth.ext.spring.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;

@Slf4j
public class AuthUserResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(AuthenticatedUser.class) != null;
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) throws Exception {
        var user = webRequest.getAttribute(AuthFilter.AUTH_USER_ATTRIBUTE, SCOPE_REQUEST);
        if (parameter.getParameterAnnotation(AuthenticatedUser.class) != null) {
            if (user == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
            } else {
                if (user instanceof Optional<?>) {
                    return ((Optional<?>) user)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
                } else {
                    return user;
                }
            }
        } else {
            throw new RuntimeException("Unsupported annotation"); // we should not get there
        }
    }
}
