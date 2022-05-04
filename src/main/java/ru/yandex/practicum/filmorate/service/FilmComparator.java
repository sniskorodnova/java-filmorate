package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Comparator;

/**
 * Компаратор для сравнения двух фильмов. Фильмы с наибольшим количеством лайков находятся выше.
 * Если у фильмов одинаковое количество лайков, то они сортируются по имени
 */
public class FilmComparator implements Comparator<Film> {
    @Override
    public int compare(Film o1, Film o2) {
        if (o1.getLikesFromUsers() == null) {
            if (o2.getLikesFromUsers() == null) {
                return o1.getName().compareTo(o2.getName());
            } else {
                return 1;
            }
        } else {
            if (o2.getLikesFromUsers() == null) {
                return -1;
            } else {
                if (o1.getLikesFromUsers().size() == o2.getLikesFromUsers().size()) {
                    return o1.getName().compareTo(o2.getName());
                } else {
                    return o2.getLikesFromUsers().size() - o1.getLikesFromUsers().size();
                }
            }
        }
    }
}
