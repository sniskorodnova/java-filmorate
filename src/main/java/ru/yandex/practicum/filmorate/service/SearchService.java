package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.search.KindOfSearchFilm;
import ru.yandex.practicum.filmorate.storage.search.Search;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {
    private final Search search;

    public List<Film> searchFilmByParam(String query, List<String> by) {
        return search.searchFilmByParam(query, byParamToKindOfSearchFilm(by));
    }

    private KindOfSearchFilm byParamToKindOfSearchFilm(List<String> by) {
        if (by.size() == 2) {
            return KindOfSearchFilm.DIRECTOR_AND_TITLE;
        } else if (by.get(0).equals("director")) {
            return KindOfSearchFilm.DIRECTOR;
        } else {
            return KindOfSearchFilm.TITLE;
        }
    }
}
