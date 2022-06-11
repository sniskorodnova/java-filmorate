package ru.yandex.practicum.filmorate.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import ru.yandex.practicum.filmorate.model.Film;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class FilmServiceAspect {

    @Pointcut("execution(* ru.yandex.practicum.filmorate.service.FilmService.likeFilm(..))")
    public void likeFilm() {}

    /*
    @After("likeFilm()")
    public void afterLikeFilm(JoinPoint joinPoint) {
        Object[] signatureArgs = joinPoint.getArgs();
        Long filmId = (Long)signatureArgs[0];
        Long userId = (Long)signatureArgs[1];
        // Здесь мы будем писать в базу
        System.out.println("userId " + userId + " поставил лайк фильму с filmId: " + filmId);
    }
    */

    // Лучше использовать так
    @Around("likeFilm()")
    public void onLikeFilm(ProceedingJoinPoint joinPoint) throws Throwable {

        Object output = joinPoint.proceed();

        Object[] signatureArgs = joinPoint.getArgs();
        Long filmId = (Long)signatureArgs[0];
        Long userId = (Long)signatureArgs[1];
        // Здесь мы будем писать в базу
        System.out.println("userId " + userId + " поставил лайк фильму с filmId: " + filmId);

    }




}
