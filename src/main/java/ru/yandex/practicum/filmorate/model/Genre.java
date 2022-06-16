package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

/**
 * Класс, описывающий сущность жанр
 */
@Data
@Builder
public class Genre {
    private int id;
    private String name;
}
