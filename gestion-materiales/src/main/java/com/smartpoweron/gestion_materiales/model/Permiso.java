package com.smartpoweron.gestion_materiales.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "permiso")
public class Permiso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_permiso")
    private Long idPermiso;

    @Column(unique = true, nullable = false, length = 50)
    private String codigo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;
}
