package com.daw.wallapop.service;

import com.daw.wallapop.entity.Anuncio;
import com.daw.wallapop.entity.FotoAnuncio;
import com.daw.wallapop.repository.AnuncioRepository;
import com.daw.wallapop.repository.FotoRepository;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FotoAnuncioService {
    private static final List<String> PERMITTED_TYPES = List.of("image/jpeg", "image/png", "image/gif", "image/avif", "image/webp");
    private static final long MAX_FILE_SIZE = 10000000;
    private static final String UPLOADS_DIRECTORY = "uploads/imagesAnuncios";

    @Autowired
    private AnuncioRepository anuncioRepository;

    @Autowired
    private FotoRepository fotoRepository;

    public void eliminarFotos(Long idAnuncio) {
        List<FotoAnuncio> fotosAnuncio = anuncioRepository.findById(idAnuncio).get().getFotos();
        if(!fotosAnuncio.isEmpty()){
            for(FotoAnuncio foto : fotosAnuncio){
                Path archivoFoto = Paths.get(System.getProperty("user.dir") + "/uploads/imagesAnuncios/" + foto.getNombre());
                try {
                    Files.deleteIfExists(archivoFoto);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            fotoRepository.deleteById(fotosAnuncio.get(0).getId());
        }
    }

    public void eliminarFoto(Long idFoto) {
        FotoAnuncio foto = fotoRepository.findById(idFoto).get();
        if(!foto.getNombre().isEmpty()){
            Path archivoFoto = Paths.get(System.getProperty("user.dir") + "/uploads/imagesAnuncios/" + foto.getNombre());
            try {
                Files.deleteIfExists(archivoFoto);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            fotoRepository.deleteById(idFoto);
        }
    }

    public void guardarFotos(List<MultipartFile> fotos, Anuncio anuncio) {
        List<FotoAnuncio> listaFotos = new ArrayList<>();

        // Directorio temporal donde almacenar las fotos antes de redimensionarlas
        Path directorioTemporal = Paths.get(System.getProperty("java.io.tmpdir"));

        for (MultipartFile foto : fotos) {
            if (!foto.isEmpty()) {
                validarArchivo(foto);  // Validar el archivo
                String nombreFoto = generarNombreUnico(foto);  // Generar un nombre único

                // Rutas del archivo temporal y el archivo final
                Path rutaTemporal = directorioTemporal.resolve(nombreFoto);
                Path rutaFinal = Paths.get(UPLOADS_DIRECTORY, nombreFoto);

                try {
                    // Guardar el archivo original en el directorio temporal
                    foto.transferTo(rutaTemporal.toFile());

                    // Redimensionar la imagen y guardarla en la ubicación final
                    redimensionarImagen(rutaTemporal.toFile(), rutaFinal.toFile(), 1000);

                    // Eliminar el archivo temporal después de redimensionarlo
                    Files.deleteIfExists(rutaTemporal);

                    // Crear entidad FotoAnuncio para asociar con el anuncio
                    FotoAnuncio fotoAnuncio = FotoAnuncio.builder()
                            .nombre(nombreFoto)
                            .anuncio(anuncio)
                            .build();

                    listaFotos.add(fotoAnuncio);

                } catch (IOException e) {
                    throw new RuntimeException("Error al guardar y redimensionar la imagen: " + e.getMessage(), e);
                }
            }
        }

        // Asociar las fotos redimensionadas con el anuncio
        anuncio.setFotos(listaFotos);
    }

    public void guardarFoto(MultipartFile foto, Anuncio anuncio) {
        // Directorio temporal donde almacenar las fotos antes de redimensionarlas
        Path directorioTemporal = Paths.get(System.getProperty("java.io.tmpdir"));
        FotoAnuncio fotoAnuncio = new FotoAnuncio();

            if (!foto.isEmpty()) {
                validarArchivo(foto);  // Validar el archivo
                String nombreFoto = generarNombreUnico(foto);  // Generar un nombre único

                // Rutas del archivo temporal y el archivo final
                Path rutaTemporal = directorioTemporal.resolve(nombreFoto);
                Path rutaFinal = Paths.get(UPLOADS_DIRECTORY, nombreFoto);

                try {
                    // Guardar el archivo original en el directorio temporal
                    foto.transferTo(rutaTemporal.toFile());

                    // Redimensionar la imagen y guardarla en la ubicación final
                    redimensionarImagen(rutaTemporal.toFile(), rutaFinal.toFile(), 1000);

                    // Eliminar el archivo temporal después de redimensionarlo
                    Files.deleteIfExists(rutaTemporal);

                    // Crear entidad FotoAnuncio para asociar con el anuncio
                    fotoAnuncio = FotoAnuncio.builder()
                            .nombre(nombreFoto)
                            .anuncio(anuncio)
                            .build();

                } catch (IOException e) {
                    throw new RuntimeException("Error al guardar y redimensionar la imagen: " + e.getMessage(), e);
                }
            }

        // Asociar las fotos redimensionadas con el anuncio
        anuncio.getFotos().add(fotoAnuncio);
    }


    public static void validarArchivo(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Archivo no seleccionado");
        }
        if (!PERMITTED_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("El archivo seleccionado no es una imagen.");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Archivo demasiado grande. Sólo se admiten archivos < 10MB");
        }
    }

    public static String generarNombreUnico(MultipartFile file) {
        UUID nombreUnico = UUID.randomUUID();
        String extension;
        if (file.getOriginalFilename() != null && !file.getOriginalFilename().isEmpty()) {
            extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        } else {
            throw new IllegalArgumentException("El archivo seleccionado no es una imagen.");
        }
        return nombreUnico + extension;
    }

    public static void guardarArchivo(MultipartFile file, String nuevoNombreFoto) {
        Path ruta = Paths.get(UPLOADS_DIRECTORY + File.separator + nuevoNombreFoto);
        //Movemos el archivo a la carpeta y guardamos su nombre en el objeto catgoría
        try {
            byte[] contenido = file.getBytes();
            Files.write(ruta, contenido);
        } catch (
                IOException e) {
            throw new RuntimeException("Error al guardar archivo", e);
        }
    }

    public void redimensionarImagen(File rutaOriginal, File rutaRedimensionada, int ancho) throws IOException {
        Thumbnails.of(rutaOriginal)
                .width(ancho)
                .keepAspectRatio(true)
                .toFile(rutaRedimensionada);
    }
}
