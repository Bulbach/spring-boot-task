package ru.clevertec.ecl.cache.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import ru.clevertec.ecl.cache.AbstractCache;
import ru.clevertec.ecl.dto.responseDto.ResponseDtoPerson;

import java.util.UUID;
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class PersonServiceCachingAspect {

    private final AbstractCache<UUID, ResponseDtoPerson> personCache;

    @Pointcut("@annotation(ru.clevertec.ecl.cache.annotation.CustomCachableGet)")
    public void getId() {
    }

    @Around(value = "getId()")
    public Object cachePerson(ProceedingJoinPoint joinPoint) throws Throwable {

        UUID id = (UUID) joinPoint.getArgs()[0];

        if (personCache.containsKey(id)) {
            return personCache.get(id);
        } else {
            ResponseDtoPerson person = (ResponseDtoPerson) joinPoint.proceed();
            log.info("Founded cache Person in repository");
            personCache.put(id, person);
            return person;
        }
    }

    @Pointcut("@annotation(ru.clevertec.ecl.cache.annotation.CustomCachebleCreate) ")
    public void create() {
    }

    @Around(value = "create()")
    public Object cacheCreate(ProceedingJoinPoint joinPoint) throws Throwable {

        ResponseDtoPerson createdPerson = (ResponseDtoPerson) joinPoint.proceed();
        log.info("Created cache Person " + createdPerson);
        personCache.put(UUID.fromString(createdPerson.uuid()), createdPerson);

        return createdPerson;
    }

    @Pointcut("@annotation(ru.clevertec.ecl.cache.annotation.CustomCachebleUpdate)")
    public void update() {
    }

    @Around(value = "update()")
    public Object cacheUpdate(ProceedingJoinPoint joinPoint) throws Throwable {

        ResponseDtoPerson updatePerson = (ResponseDtoPerson) joinPoint.proceed();
        log.info("Updated cache Person " + updatePerson);
        personCache.put(UUID.fromString(updatePerson.uuid()), updatePerson);
        return updatePerson;
    }

    @Pointcut("@annotation(ru.clevertec.ecl.cache.annotation.CustomCachebleDelete)")
    public void delete() {
    }

    @Around(value = "delete()")
    public Object cacheDelete(ProceedingJoinPoint joinPoint) throws Throwable {

        UUID id = (UUID) joinPoint.getArgs()[0];
        log.info("Deleted cache Person with id = " + id);
        personCache.delete(id);

        return joinPoint.proceed();
    }
}
