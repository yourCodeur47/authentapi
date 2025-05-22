package io.yorosoft.authentapi.core.aspect;

import io.yorosoft.authentapi.exception.LogTracingAspectException;
import jakarta.annotation.PostConstruct;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Aspect
@Component
@ConditionalOnProperty(name = "authentapi.LogTracingAspect.enable", havingValue = "true")
public class LogTracingAspect {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LogTracingAspect.class);

    @PostConstruct
    public void init() {
        log.info("LogTracingAspect initialised (authentapi.LogTracingAspect.enable=true)");
    }

    @Around("execution(public * io.yorosoft.*.service.impl..*(..))")
    public Object loggingAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        Logger targetLog = getTargetLogger(joinPoint).orElse(log);
        try {
            // Code execution
            long startTime = System.currentTimeMillis();
            Object result = joinPoint.proceed();

            if (targetLog.isTraceEnabled()) {
                // Log after with result if logging is enabled
                targetLog.trace("{} = {} in {}ms", getTargetSignature(joinPoint), Objects.toString(result, null),
                        System.currentTimeMillis() - startTime);
            }
            return result;

        } catch (Exception e) {
            targetLog.error("{} KO", getTargetSignature(joinPoint), e);
            String context = String.format("Erreur dans %s.%s avec paramètres %s",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    Arrays.toString(joinPoint.getArgs())
            );
            throw new LogTracingAspectException(context, e); // NOSONAR
        }
    }

    /**
     * @return the method signature matched by this JoinPoint
     */
    private String getTargetSignature(ProceedingJoinPoint joinPoint) {
        try {
            CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();
            // Get parameters string
            String paramStr = "";
            if (codeSignature.getParameterNames() != null) {
                paramStr = IntStream.range(0, codeSignature.getParameterNames().length).mapToObj(parameterIndex -> {
                    String parameterName = codeSignature.getParameterNames()[parameterIndex];
                    Object parameterObj = joinPoint.getArgs()[parameterIndex];
                    return parameterName + "='" + Objects.toString(parameterObj, null) + "'";
                }).collect(Collectors.joining(", "));
            }
            return codeSignature.getName() + "(" + paramStr + ")";
        } catch (Exception e) {
            log.error("getTargetSignature(...) KO", e);
            throw e;
        }
    }

    /**
     * @return the target logger if present
     */
    private Optional<Logger> getTargetLogger(JoinPoint joinPoint) {
        Object target = joinPoint.getTarget();
        Logger targetLog = null;
        try {
            Field logField = target.getClass().getDeclaredField("log");
            logField.setAccessible(true);
            targetLog = (Logger) logField.get(target);

        } catch (NoSuchFieldException e) {
            // Nothing to do, no log on target
        } catch (Exception e) {
            log.error("getTargetLogger(joinPoint.target.class='{}') KO", target.getClass().getSimpleName(), e);
            return Optional.empty();
        }
        return Optional.ofNullable(targetLog);
    }
}
