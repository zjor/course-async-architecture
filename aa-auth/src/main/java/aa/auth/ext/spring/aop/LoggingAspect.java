package aa.auth.ext.spring.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    @Around("@annotation(Log)")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        var signature = (MethodSignature) joinPoint.getSignature();
        var type = signature.getDeclaringType();
        var log = LoggerFactory.getLogger(type);
        var annotation = signature.getMethod().getAnnotation(Log.class);

        String[] paramNames = signature.getParameterNames();
        Object[] paramValues = joinPoint.getArgs();

        StringBuilder args = new StringBuilder();
        for (int i = 0; i < paramNames.length; i++) {
            args.append(paramNames[i]).append('=').append(paramValues[i]).append(',').append(' ');
        }
        if (args.length() > 2) {
            args.setLength(args.length() - 2);
        }
        try {
            Object result = joinPoint.proceed();

            if (annotation.logReturnClassname()) {
                String className = result != null ? result.getClass().getSimpleName() : null;
                log.info("{}({}): {}", signature.getName(), args, className);
            } else {
                log.info("{}({}): {}", signature.getName(), args, result);
            }

            return result;
        } catch (Throwable t) {
            log.info("{}({}) threw {}", signature.getName(), args, t.toString());
            throw t;
        }
    }

}
