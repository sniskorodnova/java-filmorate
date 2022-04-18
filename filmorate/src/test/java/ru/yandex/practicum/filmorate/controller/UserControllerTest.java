package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    UserController userController;

    @BeforeEach
    public void beforeEach() {
        userController = new UserController();
    }

    @AfterEach
    public void afterEach() {
        User.setCounter(new AtomicLong(0));
    }

    @Test
    public void createUserEmptyEmailThrowsValidationException() {
        User user = new User();
        user.setEmail("");
        user.setName("UserName");
        user.setBirthday(LocalDate.of(1990, 6, 9));
        user.setLogin("UserLogin");
        ValidationException ex = assertThrows(ValidationException.class, () -> userController.create(user));
        assertEquals("Почта пользователя не может быть пустой и должна содержать символ @", ex.getMessage());
    }

    @Test
    public void createUserInvalidEmailThrowsValidationException() {
        User user = new User();
        user.setEmail("qwerty.wsx");
        user.setName("UserName");
        user.setBirthday(LocalDate.of(1990, 6, 9));
        user.setLogin("UserLogin");
        ValidationException ex = assertThrows(ValidationException.class, () -> userController.create(user));
        assertEquals("Почта пользователя не может быть пустой и должна содержать символ @", ex.getMessage());
    }

    @Test
    public void createUserValidEmailSuccess() {
        User user = new User();
        user.setEmail("qwerty@gmail.com");
        user.setName("UserName");
        user.setBirthday(LocalDate.of(1990, 6, 9));
        user.setLogin("UserLogin");
        userController.create(user);
        assertEquals(1, userController.getAll().size());
    }

    @Test
    public void createUserEmptyLoginThrowsValidationException() {
        User user = new User();
        user.setEmail("qwerty@gmail.com");
        user.setName("UserName");
        user.setBirthday(LocalDate.of(1990, 6, 9));
        user.setLogin("");
        ValidationException ex = assertThrows(ValidationException.class, () -> userController.create(user));
        assertEquals("Логин пользователя не может быть пустым и не должен содержать пробелов",
                ex.getMessage());
    }

    @Test
    public void createUserLoginHasSpacesThrowsValidationException() {
        User user = new User();
        user.setEmail("qwerty@gmail.com");
        user.setName("UserName");
        user.setBirthday(LocalDate.of(1990, 6, 9));
        user.setLogin("User Login");
        ValidationException ex = assertThrows(ValidationException.class, () -> userController.create(user));
        assertEquals("Логин пользователя не может быть пустым и не должен содержать пробелов",
                ex.getMessage());
    }

    @Test
    public void createUserValidLoginSuccess() {
        User user = new User();
        user.setEmail("qwerty@gmail.com");
        user.setName("UserName");
        user.setBirthday(LocalDate.of(1990, 6, 9));
        user.setLogin("UserLogin");
        userController.create(user);
        assertEquals(1, userController.getAll().size());
    }

    @Test
    public void createUserInvalidBirthdayThrowsValidationException() {
        User user = new User();
        user.setEmail("qwerty@gmail.com");
        user.setName("UserName");
        user.setBirthday(LocalDate.now().plusDays(1));
        user.setLogin("UserLogin");
        ValidationException ex = assertThrows(ValidationException.class, () -> userController.create(user));
        assertEquals("День рождения пользователя не может быть в будущем", ex.getMessage());
    }

    @Test
    public void createUserValidBirthdaySuccess() {
        User user = new User();
        user.setEmail("qwerty@gmail.com");
        user.setName("UserName");
        user.setBirthday(LocalDate.now().minusDays(1));
        user.setLogin("UserLogin");
        userController.create(user);
        assertEquals(1, userController.getAll().size());
    }

    @Test
    public void createUserNameIsEmptyNameEqualsLogin() {
        User user = new User();
        user.setEmail("qwerty@gmail.com");
        user.setBirthday(LocalDate.of(1990, 6, 9));
        user.setLogin("UserLogin");
        userController.create(user);
        assertEquals("UserLogin", userController.getAll().get(0).getName());
    }

    @Test
    public void createUserNameHasOnlySpacesNameEqualsLogin() {
        User user = new User();
        user.setEmail("qwerty@gmail.com");
        user.setBirthday(LocalDate.of(1990, 6, 9));
        user.setLogin("UserLogin");
        user.setName("   ");
        userController.create(user);
        assertEquals("UserLogin", userController.getAll().get(0).getName());
    }

    @Test
    public void updateUserNewEmptyEmailThrowsValidationException() {
        User user1 = new User();
        user1.setEmail("qwerty@gmail.com");
        user1.setName("UserName");
        user1.setBirthday(LocalDate.of(1990, 6, 9));
        user1.setLogin("UserLogin");
        User user2 = new User();
        user2.setEmail("");
        user2.setName("UserName");
        user2.setBirthday(LocalDate.of(1990, 6, 9));
        user2.setLogin("UserLogin");
        userController.create(user1);
        ValidationException ex = assertThrows(ValidationException.class, () -> userController.update(user2));
        assertEquals("Почта пользователя не может быть пустой и должна содержать символ @", ex.getMessage());
    }

    @Test
    public void updateUserNewInvalidEmailThrowsValidationException() {
        User user1 = new User();
        user1.setEmail("qwerty@gmail.com");
        user1.setName("UserName");
        user1.setBirthday(LocalDate.of(1990, 6, 9));
        user1.setLogin("UserLogin");
        User user2 = new User();
        user2.setEmail("gmail.com");
        user2.setName("UserName");
        user2.setBirthday(LocalDate.of(1990, 6, 9));
        user2.setLogin("UserLogin");
        userController.create(user1);
        ValidationException ex = assertThrows(ValidationException.class, () -> userController.update(user2));
        assertEquals("Почта пользователя не может быть пустой и должна содержать символ @", ex.getMessage());
    }

    @Test
    public void updateUserNewEmptyLoginThrowsValidationException() {
        User user1 = new User();
        user1.setEmail("qwerty@gmail.com");
        user1.setName("UserName");
        user1.setBirthday(LocalDate.of(1990, 6, 9));
        user1.setLogin("UserLogin");
        User user2 = new User();
        user2.setEmail("qwerty@gmail.com");
        user2.setName("UserName");
        user2.setBirthday(LocalDate.of(1990, 6, 9));
        user2.setLogin("");
        userController.create(user1);
        ValidationException ex = assertThrows(ValidationException.class, () -> userController.update(user2));
        assertEquals("Логин пользователя не может быть пустым и не должен содержать пробелов",
                ex.getMessage());
    }

    @Test
    public void updateUserNewLoginHasSpacesThrowsValidationException() {
        User user1 = new User();
        user1.setEmail("qwerty@gmail.com");
        user1.setName("UserName");
        user1.setBirthday(LocalDate.of(1990, 6, 9));
        user1.setLogin("UserLogin");
        User user2 = new User();
        user2.setEmail("qwerty@gmail.com");
        user2.setName("UserName");
        user2.setBirthday(LocalDate.of(1990, 6, 9));
        user2.setLogin("Log in");
        userController.create(user1);
        ValidationException ex = assertThrows(ValidationException.class, () -> userController.update(user2));
        assertEquals("Логин пользователя не может быть пустым и не должен содержать пробелов",
                ex.getMessage());
    }

    @Test
    public void updateUserNewBirthdayIsAfterNowThrowsValidationException() {
        User user1 = new User();
        user1.setEmail("qwerty@gmail.com");
        user1.setName("UserName");
        user1.setBirthday(LocalDate.of(1990, 6, 9));
        user1.setLogin("UserLogin");
        User user2 = new User();
        user2.setEmail("qwerty@gmail.com");
        user2.setName("UserName");
        user2.setBirthday(LocalDate.now().plusDays(1));
        user2.setLogin("UserLogin");
        userController.create(user1);
        ValidationException ex = assertThrows(ValidationException.class, () -> userController.update(user2));
        assertEquals("День рождения пользователя не может быть в будущем", ex.getMessage());
    }

    @Test
    public void updateUserNewNameIsEmptyNameEqualsLogin() {
        User user1 = new User();
        user1.setEmail("qwerty@gmail.com");
        user1.setName("UserName");
        user1.setBirthday(LocalDate.of(1990, 6, 9));
        user1.setLogin("UserLogin");
        User user2 = new User();
        user2.setEmail("qwerty@gmail.com");
        user2.setName("");
        user2.setBirthday(LocalDate.of(1990, 6, 9));
        user2.setLogin("UserLogin");
        userController.create(user1);
        userController.update(user2);
        assertEquals("UserLogin", userController.getAll().get(0).getName());
    }

    @Test
    public void updateUserSuccess() {
        User user1 = new User();
        user1.setEmail("qwerty@gmail.com");
        user1.setName("UserName");
        user1.setBirthday(LocalDate.of(1990, 6, 9));
        user1.setLogin("UserLogin");
        User user2 = new User();
        user2.setId(1L);
        user2.setEmail("zxcvb@gmail.com");
        user2.setName("NewNameUser");
        user2.setBirthday(LocalDate.of(1988, 5, 9));
        user2.setLogin("NewUserLogin");
        userController.create(user1);
        userController.update(user2);
        assertEquals(1, userController.getAll().size());
        assertEquals(user2, userController.getAll().get(0));
    }

    @Test
    public void createAndGetTwoUsers() {
        User user1 = new User();
        user1.setEmail("qwerty@gmail.com");
        user1.setName("UserName");
        user1.setBirthday(LocalDate.of(1990, 6, 9));
        user1.setLogin("UserLogin");
        User user2 = new User();
        user2.setEmail("zxcvb@gmail.com");
        user2.setName("NewNameUser");
        user2.setBirthday(LocalDate.of(1988, 5, 9));
        user2.setLogin("NewUserLogin");
        userController.create(user1);
        userController.create(user2);
        assertEquals(2, userController.getAll().size());
        assertEquals(user1, userController.getAll().get(0));
        assertEquals(user2, userController.getAll().get(1));
    }
}