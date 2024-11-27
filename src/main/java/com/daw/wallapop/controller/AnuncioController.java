package com.daw.wallapop.controller;

import com.daw.wallapop.entity.Anuncio;
import com.daw.wallapop.entity.FotoAnuncio;
import com.daw.wallapop.entity.Usuario;
import com.daw.wallapop.repository.AnuncioRepository;
import com.daw.wallapop.repository.FotoRepository;
import com.daw.wallapop.repository.UsuarioRepository;
import com.daw.wallapop.service.AnuncioService;
import com.daw.wallapop.service.FotoAnuncioService;
import com.daw.wallapop.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Optional;

@Controller
public class AnuncioController {

    @Autowired
    private AnuncioRepository anuncioRepository;

    @Autowired
    private AnuncioService anuncioService;

    @Autowired
    private FotoAnuncioService fotoAnuncioService;

    @Autowired
    private FotoRepository fotoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/")
    public String verAnuncios(Model model) {

        List<Anuncio> anuncios = anuncioRepository.findAll();
        int totalAnuncios = anuncioService.contarAnuncios(anuncios);

        model.addAttribute("anuncios", anuncioService.findAnunciosOrdenDesc());
        model.addAttribute("totalAnuncios", totalAnuncios);
        model.addAttribute("usuario", new Usuario());

        return "anuncio-list";
    }

    @GetMapping("/misanuncios")
    public String verMisAnuncios(Model model) {

        Usuario u = (Usuario) usuarioService.getUserAuth();

        Usuario usuario = usuarioRepository.findById(u.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        model.addAttribute("anuncios", usuario.getAnuncios());
        model.addAttribute("totalAnuncios", anuncioService.contarAnuncios(usuario.getAnuncios()));
        return "misanuncios-list";
    }

    @GetMapping("/anuncios/new")
    public String verNewAnuncio(Model model) {
        model.addAttribute("anuncio", new Anuncio());
        return "anuncio-new";
    }

    @PostMapping("/anuncios/new")
    public String newAnuncio(Model model, @Valid @ModelAttribute Anuncio anuncio,
                             @RequestParam("files") List<MultipartFile> fotos,
                             BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return "anuncio-new"; // Volver a la vista con los errores de validación
        }

        // Obtener el usuario autenticado
        Usuario usuario = (Usuario) usuarioService.getUserAuth();
        anuncio.setUsuario(usuario);

        return anuncioService.guardarTodo(usuario, fotos, anuncio, model);
    }

    @PostMapping("/anuncios/del/{id}")
    public String delAnuncio(Model model, @PathVariable Long id, HttpServletRequest request) {
        try {
            if (!request.getMethod().equalsIgnoreCase("POST")) {
                return "redirect:/"; // Redirige si no es un POST
            }
            // Obtener el usuario autenticado
            Usuario usuarioAutenticado = (Usuario) usuarioService.getUserAuth();

            // Buscar el anuncio por ID
            Optional<Anuncio> anuncioOptional = anuncioRepository.findById(id);

            // Si el anuncio no existe, redirigir a la raíz
            if (anuncioOptional.isEmpty()) {
                return "redirect:/"; // Redirige a la raíz si no se encuentra el anuncio
            }

            Anuncio anuncio = anuncioOptional.get();

            // Verificar si el usuario autenticado es el propietario del anuncio
            if (anuncio.getUsuario().getId().equals(usuarioAutenticado.getId())) {
                // Eliminar las fotos y el anuncio si es el propietario
                fotoAnuncioService.eliminarFotos(id);
                anuncioRepository.deleteById(id);
            }

            // Redirigir a la raíz después de la eliminación (o si no era el propietario)
            return "redirect:/";
        } catch (Exception e) {
            // Loguear el error (opcional)
            e.printStackTrace();

            // Puedes redirigir a una página de error o mostrar un mensaje en caso de excepción
            model.addAttribute("error", "Ha ocurrido un error al intentar eliminar el anuncio.");
            return "redirect:/"; // Redirigir a la página principal si ocurre un error
        }
    }


    @GetMapping("/anuncios/edit/{id}")
    public String verEditAnuncio(Model model, @PathVariable String id) {

        // Convertir el id a Long
        Long idAnuncio = Long.parseLong(id);

        Optional<Anuncio> anuncioOptional = anuncioRepository.findById(idAnuncio);

        Anuncio anuncio = anuncioOptional.get();

        Long id_anuncio = anuncio.getId();
        // Verificar si el id contiene caracteres no numéricos (cualquier cosa que no sea un número)
        if (!id.matches("\\d+")) {
            // Si contiene caracteres especiales o no es un número, redirige a la raíz
            return "redirect:/anuncios/edit/" + id_anuncio;
        }

            // Obtener el usuario autenticado
            Usuario usuarioAutenticado = (Usuario) usuarioService.getUserAuth();

            // Si no existe el anuncio, redirige a la raíz

        // Verificamos si el usuario autenticado es el dueño del anuncio
            if (anuncio.getUsuario().getId().equals(usuarioAutenticado.getId())) {
                model.addAttribute("anuncio", anuncio);  // Pasamos el objeto a la vista
                return "anuncio-edit";  // Página para editar el anuncio
            }

        // Si no es el dueño del anuncio o no se encuentra el anuncio
        return "redirect:/";
    }



    @PostMapping("/anuncios/edit/{id}")
    public String editAnuncio(@PathVariable Long id,
                              @RequestParam("precio") Double precio,
                              @RequestParam("titulo") String titulo,
                              @RequestParam("descripcion") String descripcion) {

        Anuncio anuncio = anuncioRepository.findById(id).get();

        anuncio.setPrecio(precio);
        anuncio.setTitulo(titulo);
        anuncio.setDescripcion(descripcion);

        anuncioRepository.save(anuncio);
        return "redirect:/";
    }

    @PostMapping("/anuncios/edit/{id}/addfoto")
    public String editAnuncioAddFoto(@PathVariable Long id,
                                     @RequestParam("archivoFoto") MultipartFile foto) {

        Anuncio anuncio = anuncioRepository.findById(id).get();

        fotoAnuncioService.guardarFoto(foto, anuncio);
        anuncioRepository.save(anuncio);

        return "redirect:/anuncios/edit/{id}";
    }

    @GetMapping("/anuncios/fotos/delete/{id}")
    public String delFotoAnuncio(@PathVariable String id) {

        Long idFoto = Long.parseLong(id);

        // Verifica si la foto existe
        Optional<FotoAnuncio> optionalFoto = fotoRepository.findById(idFoto);

        FotoAnuncio foto = optionalFoto.get();

        // Obtén el ID del anuncio relacionado
        Anuncio anuncio = foto.getAnuncio();

        Long id_anuncio = anuncio.getId();

        if (!id.matches("\\d+")) {
            // Si contiene caracteres especiales o no es un número, redirige a la raíz
            return "redirect:/anuncios/edit/" + id_anuncio;
        }

        // Intenta eliminar la foto
        try {
            fotoAnuncioService.eliminarFoto(idFoto);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error eliminando la foto", e);
        }

        // Redirige al formulario de edición
        return "redirect:/anuncios/edit/" + id_anuncio;
    }


    @GetMapping("/anuncios/ver/{id}")
    public String verFotoAnuncio(@PathVariable("id") String id, Model model) {
        Long idAnuncio = Long.parseLong(id);

        // Verifica si la foto existe
        Optional<Anuncio> optionalAnuncio = anuncioRepository.findById(idAnuncio);

        if (optionalAnuncio.isPresent()) {
            Anuncio anuncio = optionalAnuncio.get();

            // Validación de id numérico
            if (!id.matches("\\d+")) {
                return "redirect:/anuncios/edit/" + anuncio.getId();
            }

            Usuario usuario = anuncio.getUsuario();

            // Agregar atributos al modelo
            model.addAttribute("anuncio", anuncio);
            model.addAttribute("usuario", usuario);

            return "anuncio-view";
        } else {
            // Si no se encuentra la foto, puedes redirigir o mostrar un error
            return "redirect:/";  // Redirigir al inicio si no se encuentra la foto
        }
    }

}
