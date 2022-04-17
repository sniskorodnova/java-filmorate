package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import java.time.Duration;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;

@Data
public class Film {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Duration filmDuration;
    private static AtomicLong counter = new AtomicLong(0);

    public static Long setCounter() {
        return counter.incrementAndGet();
    }
}
