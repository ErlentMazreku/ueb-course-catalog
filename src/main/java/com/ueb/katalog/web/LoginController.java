package com.ueb.katalog.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// Forma e hyrjes; autentikimin e kryen Spring Security.
@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(HttpServletRequest request) {
        if (request.getUserPrincipal() != null) {
            Object lastPage = request.getSession().getAttribute("lastAuthenticatedPage");
            if (lastPage instanceof String path && path.startsWith("/")
                    && !path.startsWith("//") && !path.startsWith("/login")) {
                return "redirect:" + path;
            }
            return "redirect:/courses";
        }
        return "login";
    }
}
