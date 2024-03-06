package ru.kor.testworkoutapp.service;

import org.springframework.stereotype.Service;
import ru.kor.testworkoutapp.model.User;
import ru.kor.testworkoutapp.repository.UsersRepository;

@Service
public class UsersService {
    private UsersRepository userRepository;
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow();
    }
}
