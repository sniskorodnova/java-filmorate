package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

/**
 * Класс, описывающий рейтинг фильмов MPAA
 */
@Data
@Builder
public class Mpa {
    private int id;
    private String name;
}
