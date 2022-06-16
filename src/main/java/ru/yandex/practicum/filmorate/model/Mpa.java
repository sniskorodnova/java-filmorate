package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

/**
 * Класс, описывающий рейтинг фильмов mpa
 */
@Data
@Builder
public class Mpa {
    private int id;
    private String name;
}
