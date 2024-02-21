package ru.clevertec.ecl.cache.aspect;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;

@Aspect
@Component
public class LoggingAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * Pointcut that matches all repositories, services and Web REST endpoints.
     */
    @Pointcut("within(@org.springframework.stereotype.Repository *)" +
            " || within(@org.springframework.stereotype.Service *)" +
            " || within(@org.springframework.web.bind.annotation.RestController *)")
    public void springBeanPointcut() {
    }

    /**
     * Pointcut that matches all Spring beans in the application's main packages.
     */
    @Pointcut("within(ru.clevertec.ecl..*)")
//    +
//            " || within(ru.clevertec.ecl.service..*)" +
//            " || within(ru.clevertec.ecl.controller..*)")
    public void applicationPackagePointcut() {
    }

    /**
     * Advice that logs methods throwing exceptions.
     *
     * @param joinPoint join point for advice
     * @param e         exception
     */
    @AfterThrowing(pointcut = "applicationPackagePointcut() && springBeanPointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        log.error("Exception in {}.{}() with cause = {}", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), e.getCause() != null ? e.getCause() : "NULL");
    }

    /**
     * Advice that logs when a method is entered and exited.
     *
     * @param joinPoint join point for advice
     * @return result
     * @throws Throwable throws IllegalArgumentException
     */
    @Around("applicationPackagePointcut() && springBeanPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        if (log.isDebugEnabled()) {
            log.debug("Enter: {}.{}() with argument[s] = {}", joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
        }
        try {
            Object result = joinPoint.proceed();
            if (log.isDebugEnabled()) {
                log.debug("Exit: {}.{}() with result = {}", joinPoint.getSignature().getDeclaringTypeName(),
                        joinPoint.getSignature().getName(), result);
            }
            return result;
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument: {} in {}.{}()", Arrays.toString(joinPoint.getArgs()),
                    joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
            throw e;
        }
    }

    /**
     * Pointcut that matches all Spring MVC controllers.
     */
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerPointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    /**
     * Advice that logs incoming requests to controllers.
     *
     * @param joinPoint join point for advice
     */
    @Before("controllerPointcut()")
    public void logIncomingRequest(JoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        // Wrap the request to enable multiple reads of the input stream
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);

        // Log the request path
        log.info("Incoming request to {} from IP: {}", wrappedRequest.getRequestURI(), wrappedRequest.getRemoteAddr());

        // Log the request method
        log.info("Request method: {}", wrappedRequest.getMethod());

        // Log headers
        log.info("Request headers:");
        Enumeration<String> headerNames = wrappedRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            log.info("{}: {}", headerName, wrappedRequest.getHeader(headerName));
        }

        // Log parameters
        log.info("Request parameters:");
        wrappedRequest.getParameterMap().forEach((key, value) -> log.info("{}: {}", key, value));

        // Log body for non-GET requests
        if (!wrappedRequest.getMethod().equals("GET")) {
            try {
                byte[] content = wrappedRequest.getContentAsByteArray();
                String body = new String(content, wrappedRequest.getCharacterEncoding());
                log.info("Request body: {}", body);
            } catch (IOException e) {
                log.error("Error reading request body", e);
            }
        }
    }
}
