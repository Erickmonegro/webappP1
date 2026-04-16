package com.webapp.webappstudents.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/admin")
    public String showAdmin() {
        return "admin";       // → templates/admin.html
    }

    @GetMapping("/dashboard")
    public String showDashboard() {
        return "dashboard";
    }

    @GetMapping("/documentos")
    public String showDocumentos() {
        return "documentos";
    }

    @GetMapping("/alojamientos")
    public String showAlojamientos() {
        return "alojamientos";
    }
}