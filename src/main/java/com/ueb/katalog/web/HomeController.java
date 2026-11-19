package com.ueb.katalog.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// Faqja kryesore: hap menjëherë listën e kurseve
@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/courses";
    }
}
