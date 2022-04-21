package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.Duration;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Класс с тестами для класса FilmController
 */
class FilmControllerTest {
    FilmController filmController;

    @BeforeEach
    public void beforeEach() {
        filmController = new FilmController();
    }

    @AfterEach
    public void afterEach() {
        Film.setCounter(new AtomicLong(0));
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
}