package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Класс с тестами для класса FilmController
 */
@SpringBootTest
class FilmControllerTest {
    @Autowired
    FilmController filmController;
    @Autowired
    UserController userController;

    @AfterEach
    public void afterEach() {
        Film.setCounter(new AtomicLong(0));
        filmController.getFilmService().getFilmStorage().deleteAll();
        User.setCounter(new AtomicLong(0));
        userController.getUserService().getUserStorage().deleteAll();
    }

    @Test
    public void createFilmWithoutNameThrowsValidationException() {
        Film film = new Film();
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 2, 26));
        film.setDuration(125L);
        ValidationException ex = assertThrows(ValidationException.class, () -> filmController.create(film));
        assertEquals("Имя фильма не может быть пустым", ex.getMessage());
    }

    @Test
    public void createFilmNameConsistsOfSpacesThrowsValidationException() {
        Film film = new Film();
        film.setName(" ");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 2, 26));
        film.setDuration(125L);
        ValidationException ex = assertThrows(ValidationException.class, () -> filmController.create(film));
        assertEquals("Имя фильма не может быть пустым", ex.getMessage());
    }

    @Test
    public void createValidFilmNameSuccess() {
        Film film = new Film();
        film.setName("Name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 2, 26));
        film.setDuration(125L);
        filmController.create(film);
        assertEquals(1, filmController.getAll().size());
    }

    @Test
    public void createFilmDescriptionHas201SymbolsThrowsValidationException() {
        Film film = new Film();
        film.setName("Name");
        film.setDescription("VeeryLongDescriptionVeeryLongDescriptionVeeryLongDescriptionVeeryLongDescription" +
                "VeeryLongDescriptionVeeryLongDescriptionVeeryLongDescriptionVeeryLongDescriptionVeeryLongDescription" +
                "VeeryLongDescription1");
        film.setReleaseDate(LocalDate.of(2000, 2, 26));
        film.setDuration(125L);
        ValidationException ex = assertThrows(ValidationException.class, () -> filmController.create(film));
        assertEquals("Описание фильма не может быть больше 200 символов", ex.getMessage());
    }

    @Test
    public void createFilmDescriptionHas200SymbolsSuccess() {
        Film film = new Film();
        film.setName("Name");
        film.setDescription("VeeryLongDescriptionVeeryLongDescriptionVeeryLongDescriptionVeeryLongDescription" +
                "VeeryLongDescriptionVeeryLongDescriptionVeeryLongDescriptionVeeryLongDescriptionVeeryLongDescription" +
                "VeeryLongDescription");
        film.setReleaseDate(LocalDate.of(2000, 2, 26));
        film.setDuration(125L);
        filmController.create(film);
        assertEquals(1, filmController.getAll().size());
    }

    @Test
    public void createFilmReleaseDate27December1895ThrowsValidationException() {
        Film film = new Film();
        film.setName("Name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(125L);
        ValidationException ex = assertThrows(ValidationException.class, () -> filmController.create(film));
        assertEquals("Дата выхода фильма не может быть раньше 28 декабря 1895 года", ex.getMessage());
    }

    @Test
    public void createFilmReleaseDate29December1895Success() {
        Film film = new Film();
        film.setName("Name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1895, 12, 29));
        film.setDuration(125L);
        filmController.create(film);
        assertEquals(1, filmController.getAll().size());
    }

    @Test
    public void createFilmDurationIsZeroThrowsValidationException() {
        Film film = new Film();
        film.setName("Name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1995, 12, 27));
        film.setDuration(0L);
        ValidationException ex = assertThrows(ValidationException.class, () -> filmController.create(film));
        assertEquals("Продолжительность фильма не может быть отрицательной или равной нулю", ex.getMessage());
    }

    @Test
    public void createFilmDurationIsNegativeThrowsValidationException() {
        Film film = new Film();
        film.setName("Name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1995, 12, 27));
        film.setDuration(-150L);
        ValidationException ex = assertThrows(ValidationException.class, () -> filmController.create(film));
        assertEquals("Продолжительность фильма не может быть отрицательной или равной нулю", ex.getMessage());
    }

    @Test
    public void updateFilmNewNameIsEmptyThrowsValidationException() {
        Film film1 = new Film();
        film1.setName("Name1");
        film1.setDescription("Description1");
        film1.setReleaseDate(LocalDate.of(1995, 12, 27));
        film1.setDuration(150L);
        filmController.create(film1);
        Film film2 = new Film();
        film2.setId(1L);
        film2.setName("");
        film2.setDescription("Description2");
        film2.setReleaseDate(LocalDate.of(1995, 12, 27));
        film2.setDuration(150L);
        ValidationException ex = assertThrows(ValidationException.class, () -> filmController.update(film2));
        assertEquals("Имя фильма не может быть пустым", ex.getMessage());
    }

    @Test
    public void updateFilmNewDescriptionHas201SymbolsThrowsValidationException() {
        Film film1 = new Film();
        film1.setName("Name1");
        film1.setDescription("Description1");
        film1.setReleaseDate(LocalDate.of(1995, 12, 27));
        film1.setDuration(150L);
        filmController.create(film1);
        Film film2 = new Film();
        film2.setId(1L);
        film2.setName("Name2");
        film2.setDescription("VeeryLongDescriptionVeeryLongDescriptionVeeryLongDescriptionVeeryLongDescription" +
                "VeeryLongDescriptionVeeryLongDescriptionVeeryLongDescriptionVeeryLongDescriptionVeeryLongDescription" +
                "VeeryLongDescription1");
        film2.setReleaseDate(LocalDate.of(1995, 12, 27));
        film2.setDuration(150L);
        ValidationException ex = assertThrows(ValidationException.class, () -> filmController.update(film2));
        assertEquals("Описание фильма не может быть больше 200 символов", ex.getMessage());
    }

    @Test
    public void updateFilmNewReleaseDateIs27December1895ThrowsValidationException() {
        Film film1 = new Film();
        film1.setName("Name1");
        film1.setDescription("Description1");
        film1.setReleaseDate(LocalDate.of(1995, 12, 27));
        film1.setDuration(150L);
        filmController.create(film1);
        Film film2 = new Film();
        film2.setId(1L);
        film2.setName("Name2");
        film2.setDescription("Description2");
        film2.setReleaseDate(LocalDate.of(1895, 12, 27));
        film2.setDuration(150L);
        ValidationException ex = assertThrows(ValidationException.class, () -> filmController.update(film2));
        assertEquals("Дата выхода фильма не может быть раньше 28 декабря 1895 года", ex.getMessage());
    }

    @Test
    public void updateFilmNewDurationIsZeroThrowsValidationException() {
        Film film1 = new Film();
        film1.setName("Name1");
        film1.setDescription("Description1");
        film1.setReleaseDate(LocalDate.of(1995, 12, 27));
        film1.setDuration(150L);
        filmController.create(film1);
        Film film2 = new Film();
        film2.setId(1L);
        film2.setName("Name2");
        film2.setDescription("Description2");
        film2.setReleaseDate(LocalDate.of(2005, 12, 27));
        film2.setDuration(0L);
        ValidationException ex = assertThrows(ValidationException.class, () -> filmController.update(film2));
        assertEquals("Продолжительность фильма не может быть отрицательной или равной нулю", ex.getMessage());
    }

    @Test
    public void updateFilmSuccess() {
        Film film1 = new Film();
        film1.setName("Name1");
        film1.setDescription("Description1");
        film1.setReleaseDate(LocalDate.of(1995, 12, 27));
        film1.setDuration(150L);
        filmController.create(film1);
        Film film2 = new Film();
        film2.setId(1L);
        film2.setName("Name2");
        film2.setDescription("Description2");
        film2.setReleaseDate(LocalDate.of(2005, 12, 27));
        film2.setDuration(180L);
        filmController.update(film2);
        assertEquals(1, filmController.getAll().size());
        assertEquals(film2, filmController.getAll().get(0));
    }

    @Test
    public void createAndGetTwoFilms() {
        Film film1 = new Film();
        film1.setName("Name1");
        film1.setDescription("Description1");
        film1.setReleaseDate(LocalDate.of(1995, 12, 27));
        film1.setDuration(150L);
        filmController.create(film1);
        Film film2 = new Film();
        film2.setName("Name2");
        film2.setDescription("Description2");
        film2.setReleaseDate(LocalDate.of(2005, 12, 27));
        film2.setDuration(180L);
        filmController.create(film2);
        assertEquals(2, filmController.getAll().size());
        assertEquals(film1, filmController.getAll().get(0));
        assertEquals(film2, filmController.getAll().get(1));
    }

    @Test
    public void createAndGetFilmById() {
        Film film1 = new Film();
        film1.setName("Name1");
        film1.setDescription("Description1");
        film1.setReleaseDate(LocalDate.of(1995, 12, 27));
        film1.setDuration(150L);
        filmController.create(film1);
        assertEquals(film1, filmController.getById(1L));
    }

    @Test
    public void filmLikedByNonExistedUserThrowUserNotFoundException() {
        Film film1 = new Film();
        film1.setName("Name1");
        film1.setDescription("Description1");
        film1.setReleaseDate(LocalDate.of(1995, 12, 27));
        film1.setDuration(150L);
        filmController.create(film1);
        UserNotFoundException ex = assertThrows(UserNotFoundException.class, ()
                -> filmController.likeFilm(1L, 3L));
        assertEquals("User with id = 3 not found", ex.getMessage());
    }

    @Test
    public void userLikesNonExistedFilmThrowFilmNotFoundException() {
        User user1 = new User();
        user1.setEmail("qwerty@gmail.com");
        user1.setName("UserName");
        user1.setBirthday(LocalDate.of(1990, 6, 9));
        user1.setLogin("UserLogin");
        userController.create(user1);
        FilmNotFoundException ex = assertThrows(FilmNotFoundException.class, ()
                -> filmController.likeFilm(1L, 1L));
        assertEquals("Film with id = 1 not found", ex.getMessage());
    }

    @Test
    public void userLikesFilmSuccess() {
        User user1 = new User();
        user1.setEmail("qwerty@gmail.com");
        user1.setName("UserName");
        user1.setBirthday(LocalDate.of(1990, 6, 9));
        user1.setLogin("UserLogin");
        userController.create(user1);
        Film film1 = new Film();
        film1.setName("Name1");
        film1.setDescription("Description1");
        film1.setReleaseDate(LocalDate.of(1995, 12, 27));
        film1.setDuration(150L);
        filmController.create(film1);
        filmController.likeFilm(1L, 1L);
        Set<Long> setForTest = new HashSet<>();
        setForTest.add(1L);
        assertEquals(setForTest, filmController.getById(1L).getLikesFromUsers());
    }

    @Test
    public void deleteLikeForFilmByNonExistedUserThrowUserNotFoundException() {
        Film film1 = new Film();
        film1.setName("Name1");
        film1.setDescription("Description1");
        film1.setReleaseDate(LocalDate.of(1995, 12, 27));
        film1.setDuration(150L);
        filmController.create(film1);
        UserNotFoundException ex = assertThrows(UserNotFoundException.class, ()
                -> filmController.deleteLike(1L, 2L));
        assertEquals("User with id = 2 not found", ex.getMessage());
    }

    @Test
    public void deleteLikeForNonExistedFilmThrowFilmNotFoundException() {
        User user1 = new User();
        user1.setEmail("qwerty@gmail.com");
        user1.setName("UserName");
        user1.setBirthday(LocalDate.of(1990, 6, 9));
        user1.setLogin("UserLogin");
        userController.create(user1);
        FilmNotFoundException ex = assertThrows(FilmNotFoundException.class, ()
                -> filmController.deleteLike(1L, 1L));
        assertEquals("Film with id = 1 not found", ex.getMessage());
    }

    @Test
    public void deleteLikeForFilmSuccess() {
        User user1 = new User();
        user1.setEmail("qwerty@gmail.com");
        user1.setName("UserName");
        user1.setBirthday(LocalDate.of(1990, 6, 9));
        user1.setLogin("UserLogin");
        userController.create(user1);
        Film film1 = new Film();
        film1.setName("Name1");
        film1.setDescription("Description1");
        film1.setReleaseDate(LocalDate.of(1995, 12, 27));
        film1.setDuration(150L);
        filmController.create(film1);
        filmController.likeFilm(1L, 1L);
        filmController.deleteLike(1L, 1L);
        assertEquals(0, filmController.getById(1L).getLikesFromUsers().size());
    }

    @Test
    public void getCountAllFilmsWithoutRates() {
        Film film1 = new Film();
        film1.setName("Name2");
        film1.setDescription("Description1");
        film1.setReleaseDate(LocalDate.of(1995, 12, 27));
        film1.setDuration(150L);
        filmController.create(film1);
        Film film2 = new Film();
        film2.setName("Name1");
        film2.setDescription("Description2");
        film2.setReleaseDate(LocalDate.of(2000, 1, 2));
        film2.setDuration(140L);
        filmController.create(film2);
        List<Film> filmsToTest = new ArrayList<>();
        filmsToTest.add(film2);
        filmsToTest.add(film1);
        assertEquals(filmsToTest, filmController.getCountFilms(10));
    }

    @Test
    public void getCountOneFilmAllFilmsWithoutRates() {
        Film film1 = new Film();
        film1.setName("Name2");
        film1.setDescription("Description1");
        film1.setReleaseDate(LocalDate.of(1995, 12, 27));
        film1.setDuration(150L);
        filmController.create(film1);
        Film film2 = new Film();
        film2.setName("Name1");
        film2.setDescription("Description2");
        film2.setReleaseDate(LocalDate.of(2000, 1, 2));
        film2.setDuration(140L);
        filmController.create(film2);
        List<Film> filmsToTest = new ArrayList<>();
        filmsToTest.add(film2);
        assertEquals(filmsToTest, filmController.getCountFilms(1));
    }

    @Test
    public void getCountFilmsWithRatingsDifferentRatings() {
        Film film1 = new Film();
        film1.setName("Name2");
        film1.setDescription("Description1");
        film1.setReleaseDate(LocalDate.of(1995, 12, 27));
        film1.setDuration(150L);
        filmController.create(film1);
        Film film2 = new Film();
        film2.setName("Name1");
        film2.setDescription("Description2");
        film2.setReleaseDate(LocalDate.of(2000, 1, 2));
        film2.setDuration(140L);
        filmController.create(film2);
        User user1 = new User();
        user1.setEmail("qwerty@gmail.com");
        user1.setName("UserName");
        user1.setBirthday(LocalDate.of(1990, 6, 9));
        user1.setLogin("UserLogin");
        userController.create(user1);
        User user2 = new User();
        user2.setEmail("vbscvb@gmail.com");
        user2.setName("UserName2");
        user2.setBirthday(LocalDate.of(1985, 2, 9));
        user2.setLogin("UserLogin2");
        userController.create(user2);
        User user3 = new User();
        user3.setEmail("lkfldsf@gmail.com");
        user3.setName("UserName3");
        user3.setBirthday(LocalDate.of(1991, 2, 26));
        user3.setLogin("UserLogin3");
        userController.create(user3);
        filmController.likeFilm(2L, 1L);
        filmController.likeFilm(2L, 2L);
        filmController.likeFilm(1L, 3L);
        List<Film> filmsToTest = new ArrayList<>();
        filmsToTest.add(film2);
        filmsToTest.add(film1);
        assertEquals(filmsToTest, filmController.getCountFilms(10));
    }

    @Test
    public void getCountFilmsWithRatingsSameRatingsSortByName() {
        Film film1 = new Film();
        film1.setName("Name2");
        film1.setDescription("Description1");
        film1.setReleaseDate(LocalDate.of(1995, 12, 27));
        film1.setDuration(150L);
        filmController.create(film1);
        Film film2 = new Film();
        film2.setName("Name1");
        film2.setDescription("Description2");
        film2.setReleaseDate(LocalDate.of(2000, 1, 2));
        film2.setDuration(140L);
        filmController.create(film2);
        User user1 = new User();
        user1.setEmail("qwerty@gmail.com");
        user1.setName("UserName");
        user1.setBirthday(LocalDate.of(1990, 6, 9));
        user1.setLogin("UserLogin");
        userController.create(user1);
        User user2 = new User();
        user2.setEmail("vbscvb@gmail.com");
        user2.setName("UserName2");
        user2.setBirthday(LocalDate.of(1985, 2, 9));
        user2.setLogin("UserLogin2");
        userController.create(user2);
        User user3 = new User();
        user3.setEmail("lkfldsf@gmail.com");
        user3.setName("UserName3");
        user3.setBirthday(LocalDate.of(1991, 2, 26));
        user3.setLogin("UserLogin3");
        userController.create(user3);
        filmController.likeFilm(2L, 1L);
        filmController.likeFilm(2L, 2L);
        filmController.likeFilm(1L, 3L);
        filmController.likeFilm(1L, 2L);
        List<Film> filmsToTest = new ArrayList<>();
        filmsToTest.add(film2);
        filmsToTest.add(film1);
        assertEquals(filmsToTest, filmController.getCountFilms(10));
    }
}