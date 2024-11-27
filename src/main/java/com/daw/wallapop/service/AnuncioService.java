package com.daw.wallapop.service;

import com.daw.wallapop.entity.Anuncio;
import com.daw.wallapop.entity.Usuario;
import com.daw.wallapop.repository.AnuncioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class AnuncioService {
    @Autowired
    private AnuncioRepository anuncioRepository;

    @Autowired
    private FotoAnuncioService fotoAnuncioService;

    public void añadirAnuncio(Anuncio anuncio){
        anuncioRepository.save(anuncio);
    }

    public List<Anuncio> findAnunciosOrdenDesc() {
        return anuncioRepository.findAllByOrderByFechaDesc();
    }
    public int contarAnuncios(List<Anuncio> anuncios){
        int totalAnuncios = 0;
        for (Anuncio anuncio : anuncios) {
            totalAnuncios++;
        }
        return totalAnuncios;
    }
    public String guardarTodo(Usuario usuario, List<MultipartFile> fotos, Anuncio anuncio, Model model){
        try {
            // Guardar las fotos
            fotoAnuncioService.guardarFotos(fotos, anuncio);

            // Añadir el anuncio al usuario
            usuario.getAnuncios().add(anuncio);

            // Aquí se guarda el anuncio y la fecha se asignará automáticamente por la base de datos
            this.añadirAnuncio(anuncio);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("mensaje", ex.getMessage());
            return "anuncio-new";
        }
        return "redirect:/";
    }
}
