package com.webapp.webappstudents.controller;

import com.webapp.webappstudents.service.DocumentoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DocumentoViewController {

    @Autowired
    private DocumentoService documentoService;
    //vista del pdf subido
    @GetMapping("/documentos")
    public String verDocumentos(Model model) {
        model.addAttribute("documentos", documentoService.obtenerTodos());
        return "documentos";
    }
}