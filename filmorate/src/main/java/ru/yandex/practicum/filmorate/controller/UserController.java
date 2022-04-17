package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private HashMap<Long, User> users = new HashMap<>();

    @GetMapping
    public List<User> getAll() {
        ArrayList<User> userList = new ArrayList<>();

        for (Long id : users.keySet()) {
            userList.add(users.get(id));
        }

        return userList;
    }

    @PostMapping
    public User create(@RequestBody User user) {
        Long generatedId = User.setCounter();
        user.setId(generatedId);
        users.put(generatedId, user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        users.put(user.getId(), user);
        return user;
    }
}
