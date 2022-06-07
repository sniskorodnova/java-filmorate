package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.SearchService;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Slf4j
public class SearchFilmsTests {
    private final FilmDbStorage filmDbStorage;
    private final SearchService searchService;

    @Test
    public void checkFindFilmByTitle() {
        Film film = Film.builder().name("Крадущийся тигр").description("Description")
                .releaseDate(LocalDate.of(1995, 12, 27)).duration(120L)
                .mpa(Mpa.builder().id(3).build()).build();
        filmDbStorage.create(film);

        Film film2 = Film.builder().name("Тигр, крадущийся во тьме").description("Description")
                .releaseDate(LocalDate.of(1995, 12, 27)).duration(120L)
                .mpa(Mpa.builder().id(3).build()).build();
        filmDbStorage.create(film2);

        Film film3 = Film.builder().name("Просто новый фильм").description("Description")
                .releaseDate(LocalDate.of(1995, 12, 27)).duration(120L)
                .mpa(Mpa.builder().id(3).build()).build();
        filmDbStorage.create(film3);

        List<Film> films = searchService.searchFilmByParam("крад", List.of("title"));

        assertThat(films.size(), is(2));
    }
}
