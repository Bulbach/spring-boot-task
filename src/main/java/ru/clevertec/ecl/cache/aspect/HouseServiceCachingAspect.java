package ru.clevertec.ecl.cache.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import ru.clevertec.ecl.cache.AbstractCache;
import ru.clevertec.ecl.dto.responseDto.ResponseDtoHouse;

import java.util.UUID;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class HouseServiceCachingAspect {

    private final AbstractCache<UUID, ResponseDtoHouse> houseCache;

    @Pointcut("@annotation(ru.clevertec.ecl.cache.annotation.CustomCachableHouseGet)")
    public void getId() {
    }

    @Around(value = "getId()")
    public Object cacheHouse(ProceedingJoinPoint joinPoint) throws Throwable {

        UUID id = (UUID) joinPoint.getArgs()[0];

        if (houseCache.containsKey(id)) {
            return houseCache.get(id);
        } else {
            ResponseDtoHouse house = (ResponseDtoHouse) joinPoint.proceed();
            log.info("Founded cache house in repository");
            houseCache.put(id, house);
            return house;
        }
    }

    @Pointcut("@annotation(ru.clevertec.ecl.cache.annotation.CustomCachebleHouseCreate) ")
    public void create() {
    }

    @Around(value = "create()")
    public Object cacheCreate(ProceedingJoinPoint joinPoint) throws Throwable {

        ResponseDtoHouse createHouse = (ResponseDtoHouse) joinPoint.proceed();
        log.info("Created cache House " + createHouse);
        houseCache.put(UUID.fromString(createHouse.uuid()), createHouse);

        return createHouse;
    }

    @Pointcut("@annotation(ru.clevertec.ecl.cache.annotation.CustomCachebleHouseUpdate)")
    public void update() {
    }

    @Around(value = "update()")
    public Object cacheUpdate(ProceedingJoinPoint joinPoint) throws Throwable {

        ResponseDtoHouse updateHouse = (ResponseDtoHouse) joinPoint.proceed();
        log.info("Updated cache House " + updateHouse);
        houseCache.put(UUID.fromString(updateHouse.uuid()), updateHouse);
        return updateHouse;
    }

    @Pointcut("@annotation(ru.clevertec.ecl.cache.annotation.CustomCachebleHouseDelete)")
    public void delete() {
    }

    @Around(value = "delete()")
    public Object cacheDelete(ProceedingJoinPoint joinPoint) throws Throwable {

        UUID id = (UUID) joinPoint.getArgs()[0];
        log.info("Deleted cache House with id = " + id);
        houseCache.delete(id);

        return joinPoint.proceed();
    }
}
