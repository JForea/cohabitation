package com.example.backend.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FrontendController {
    @RequestMapping(value = "^(?!/api)(?!.*\\.(css|js)$).*")
    public String forward() {
        return "forward:/index.html";
    }
}