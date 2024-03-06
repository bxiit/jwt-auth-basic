package ru.kor.testworkoutapp.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import ru.kor.testworkoutapp.service.UsersService;

@Controller
@AllArgsConstructor
public class UsersController {
    private UsersService usersService;
}
