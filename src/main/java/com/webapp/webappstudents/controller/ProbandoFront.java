package com.webapp.webappstudents.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

public class ProbandoFront {

    @Controller
    public class LoginController {
        @GetMapping("/login")
        public String showLoginForm() {
            return "login"; // Esto busca resources/templates/login.html
        }
    }
}

@Controller
 class DashboardController {

    @GetMapping("/dashboard")
    public String showDashboard() {
        return "dashboard"; // Esto buscará templates/dashboard.html
    }
}