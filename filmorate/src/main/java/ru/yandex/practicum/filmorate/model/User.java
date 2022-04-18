package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;

@Data
public class User {
    private Long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private static AtomicLong counter = new AtomicLong(0);

    public static void setCounter(AtomicLong counter) {
        User.counter = counter;
    }

    public static Long setIdCounter() {
        return counter.incrementAndGet();
    }
}
