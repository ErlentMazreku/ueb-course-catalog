package com.ueb.katalog.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// Forma e hyrjes; autentikimin e kryen Spring Security.
@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
