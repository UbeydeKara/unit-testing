package com.ubeydekara.user.service;

import com.ubeydekara.user.domain.User;
import com.ubeydekara.user.exception.BadRequestException;
import com.ubeydekara.user.exception.UserNotFoundException;
import com.ubeydekara.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void addUser(User user) {
        Boolean existsEmail = userRepository
                .selectExistsEmail(user.getEmail());
        if (existsEmail) {
            throw new BadRequestException(
                    "Email " + user.getEmail() + " taken");
        }

        userRepository.save(user);
    }

    public void deleteUser(UUID userId) {
        if(!userRepository.existsById(userId)) {
            throw new UserNotFoundException(
                    "User with id " + userId + " does not exists");
        }
        userRepository.deleteById(userId);
    }
}
