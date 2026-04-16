package com.webapp.webappstudents.service;

import com.webapp.webappstudents.model.Documento;
import com.webapp.webappstudents.model.User;
import com.webapp.webappstudents.repository.DocumentoRepository;
import com.webapp.webappstudents.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@Service
public class DocumentoService {

    @Autowired
    private DocumentoRepository documentoRepository;

    @Autowired
    private UserRepository userRepository;


    //estudiante
    public List<Documento> obtenerPorUserId(Long userId) {
       return documentoRepository.findByUserId(userId);
    }


    // obtener por Id
    public Documento obtenerPorId(Long id) {
        return documentoRepository.findById(id).orElse(null);
    }

    //vista general (admin)
    public List<Documento> obtenerTodos() {
        return documentoRepository.findAll();
    }

    // guardar doc
    public Documento guardar(Documento documento) {
        return documentoRepository.save(documento);
    }


    // subir archivo
    public Documento guardarArchivo(MultipartFile file, Long userId) throws Exception {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User no encontrado"));

        String basePath = System.getProperty("user.dir");

        String folder = basePath + File.separator + "uploads" + File.separator + "user_" + userId;
        File dir = new File(folder);
        if (!dir.exists()) dir.mkdirs();

        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        String path = folder + "/" + filename;

        file.transferTo(new File(path));

        Documento doc = new Documento();
        doc.setTitulo(file.getOriginalFilename());
        doc.setRutaPdf(path);
        doc.setUser(user);

        System.out.println("Guardando...");

        //guardar pdf en BD
        return documentoRepository.save(doc);
    }

    // eliminar
    public void eliminar(Long id) {
        documentoRepository.deleteById(id);
    }
}
