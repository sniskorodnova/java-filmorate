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
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.userfilmlikes.UserFilmLikesDbStorage;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Slf4j
public class CommonFilmsTest {

    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;
    private final UserFilmLikesDbStorage likesDbStorage;

    @Test
    public void check_exists_common_films() {

        User user1 = User.builder()
                .name("user1")
                .email("aaa@bbb.ru")
                .login("user1")
                .birthday(LocalDate.of(1990, 6, 9))
                .build();

        User user2 = User.builder()
                .name("user2")
                .email("bbb@bbb.ru")
                .login("user2")
                .birthday(LocalDate.of(1990, 6, 9))
                .build();

        User user3 = User.builder()
                .name("user3")
                .email("ccc@bbb.ru")
                .login("user3")
                .birthday(LocalDate.of(1990, 6, 9))
                .build();

        User user4 = User.builder()
                .name("user4")
                .email("ddd@bbb.ru")
                .login("user4")
                .birthday(LocalDate.of(1990, 6, 9))
                .build();

        user1 = userDbStorage.create(user1);
        user2 = userDbStorage.create(user2);
        user3 = userDbStorage.create(user3);
        user4 = userDbStorage.create(user4);

        Film film1 = Film.builder()
                .name("Name1")
                .description("Description")
                .releaseDate(LocalDate.of(1995, 12, 27)).duration(120L)
                .mpa(Mpa.builder().id(3).build())
                .build();

        Film film2 = Film.builder()
                .name("Name2")
                .description("Description")
                .releaseDate(LocalDate.of(1995, 12, 27)).duration(120L)
                .mpa(Mpa.builder().id(3).build())
                .build();

        Film film3 = Film.builder()
                .name("Name3")
                .description("Description")
                .releaseDate(LocalDate.of(1995, 12, 27)).duration(120L)
                .mpa(Mpa.builder().id(3).build())
                .build();

        film1 = filmDbStorage.create(film1);
        film2 = filmDbStorage.create(film2);
        film3 = filmDbStorage.create(film3);

        likesDbStorage.saveLike(film3.getId(), user1.getId());
        likesDbStorage.saveLike(film3.getId(), user2.getId());
        likesDbStorage.saveLike(film3.getId(), user3.getId());
        likesDbStorage.saveLike(film3.getId(), user4.getId());

        likesDbStorage.saveLike(film2.getId(), user1.getId());
        likesDbStorage.saveLike(film2.getId(), user2.getId());

        likesDbStorage.saveLike(film1.getId(), user3.getId());
        likesDbStorage.saveLike(film1.getId(), user4.getId());

        List<Film> commonFilms = likesDbStorage.getCommonFilms(user1.getId(), user2.getId());

        assertEquals(2, commonFilms.size());
        assertEquals(film3.getId(), commonFilms.get(0).getId());
        assertEquals(film2.getId(), commonFilms.get(1).getId());

    }

    @Test
    public void check_not_exists_common_films_1() {

        User user1 = User.builder()
                .name("user1")
                .email("aaa@bbb.ru")
                .login("user1")
                .birthday(LocalDate.of(1990, 6, 9))
                .build();

        User user2 = User.builder()
                .name("user2")
                .email("bbb@bbb.ru")
                .login("user2")
                .birthday(LocalDate.of(1990, 6, 9))
                .build();

        user1 = userDbStorage.create(user1);
        user2 = userDbStorage.create(user2);

        Film film1 = Film.builder()
                .name("Name1")
                .description("Description")
                .releaseDate(LocalDate.of(1995, 12, 27)).duration(120L)
                .mpa(Mpa.builder().id(3).build())
                .build();

        Film film2 = Film.builder()
                .name("Name2")
                .description("Description")
                .releaseDate(LocalDate.of(1995, 12, 27)).duration(120L)
                .mpa(Mpa.builder().id(3).build())
                .build();

        film1 = filmDbStorage.create(film1);
        film2 = filmDbStorage.create(film2);

        likesDbStorage.saveLike(film1.getId(), user1.getId());
        likesDbStorage.saveLike(film2.getId(), user2.getId());

        List<Film> commonFilms = likesDbStorage.getCommonFilms(user1.getId(), user2.getId());

        assertEquals(0, commonFilms.size());

    }

    @Test
    public void check_not_exists_common_films_2() {

        User user1 = User.builder()
                .name("user1")
                .email("aaa@bbb.ru")
                .login("user1")
                .birthday(LocalDate.of(1990, 6, 9))
                .build();

        User user2 = User.builder()
                .name("user2")
                .email("bbb@bbb.ru")
                .login("user2")
                .birthday(LocalDate.of(1990, 6, 9))
                .build();

        user1 = userDbStorage.create(user1);
        user2 = userDbStorage.create(user2);

        Film film1 = Film.builder()
                .name("Name1")
                .description("Description")
                .releaseDate(LocalDate.of(1995, 12, 27)).duration(120L)
                .mpa(Mpa.builder().id(3).build())
                .build();

        Film film2 = Film.builder()
                .name("Name2")
                .description("Description")
                .releaseDate(LocalDate.of(1995, 12, 27)).duration(120L)
                .mpa(Mpa.builder().id(3).build())
                .build();

        film1 = filmDbStorage.create(film1);
        film2 = filmDbStorage.create(film2);

        likesDbStorage.saveLike(film1.getId(), user1.getId());
        likesDbStorage.saveLike(film2.getId(), user1.getId());

        List<Film> commonFilms = likesDbStorage.getCommonFilms(user1.getId(), user2.getId());

        assertEquals(0, commonFilms.size());

    }

    @Test
    public void check_not_exists_common_films_3() {

        User user1 = User.builder()
                .name("user1")
                .email("aaa@bbb.ru")
                .login("user1")
                .birthday(LocalDate.of(1990, 6, 9))
                .build();

        User user2 = User.builder()
                .name("user2")
                .email("bbb@bbb.ru")
                .login("user2")
                .birthday(LocalDate.of(1990, 6, 9))
                .build();

        user1 = userDbStorage.create(user1);
        user2 = userDbStorage.create(user2);

        Film film1 = Film.builder()
                .name("Name1")
                .description("Description")
                .releaseDate(LocalDate.of(1995, 12, 27)).duration(120L)
                .mpa(Mpa.builder().id(3).build())
                .build();

        Film film2 = Film.builder()
                .name("Name2")
                .description("Description")
                .releaseDate(LocalDate.of(1995, 12, 27)).duration(120L)
                .mpa(Mpa.builder().id(3).build())
                .build();

        film1 = filmDbStorage.create(film1);
        film2 = filmDbStorage.create(film2);

        List<Film> commonFilms = likesDbStorage.getCommonFilms(user1.getId(), user2.getId());

        assertEquals(0, commonFilms.size());

    }

}
