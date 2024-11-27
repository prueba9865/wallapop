package com.daw.wallapop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//Anotaciones de LomBok
@Data   //Incluye @ToString, @Getter, @Setter, @RequiredArgsConstructor y @EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder    //Patron Builder
//Anotacion de Spring Data JPA
@Entity     //Especifica que esta clase es una entidad
@Table(name="anuncios")    //Incida que la tabla en la base de datos relacionada con esta entidad
public class Anuncio {
    @Id     //Esta anotación especifica que este campo va a ser la clave principal de la tabla en la base de datos
    @GeneratedValue(strategy = GenerationType.IDENTITY)     //Esta anotación especifica que la clave primaria sea "auto-increment"
    private Long id;

    @NotNull(message = "El precio no puede estar en blanco")
    @Min(value = 0, message = "El precio debe ser positivo")
    private Double precio;

    @NotEmpty(message = "El título no puede estar en blanco")
    @Column(length = 1000)
    private String titulo;

    @NotEmpty(message = "La descripción no puede estar en blanco")
    @Column(length = 5000)
    private String descripcion;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @OneToMany(targetEntity = FotoAnuncio.class, cascade = CascadeType.ALL,
            mappedBy = "anuncio")
    private List<FotoAnuncio> fotos = new ArrayList<>();

    @ManyToOne(targetEntity = Usuario.class)
    @JoinColumn(nullable = false)
    private Usuario usuario;

    @PrePersist
    public void prePersist() {
        // Asignar la fecha actual antes de insertar el anuncio
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
    }

    @Override
    public String toString() {
        return "Anuncio{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", fecha='" + fecha + '\'' +
                ", precio=" + precio +
                '}';
    }
}
