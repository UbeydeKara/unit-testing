package com.ubeydekara.user.controller;

import com.ubeydekara.user.service.UserService;
import com.ubeydekara.user.domain.User;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "api/v1/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<User> findAll() {
        return userService.getAllUsers();
    }

    @PostMapping
    public void add(@Valid @RequestBody User user) {
        userService.addUser(user);
    }

    @DeleteMapping(path = "{userId}")
    public void delete(
            @PathVariable("userId") UUID userId) {
        userService.deleteUser(userId);
    }
}
