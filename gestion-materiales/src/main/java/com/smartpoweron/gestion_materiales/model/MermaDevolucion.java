package com.smartpoweron.gestion_materiales.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "merma_devolucion")
public class MermaDevolucion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_merma")
    private Long idMerma;

    @ManyToOne
    @JoinColumn(name = "id_proyecto", nullable = false)
    private Proyecto proyecto;

    @ManyToOne
    @JoinColumn(name = "id_material", nullable = false)
    private Material material;

    @ManyToOne
    @JoinColumn(name = "id_usuario_registra", nullable = false)
    private Usuario usuarioRegistra;

    @ManyToOne
    @JoinColumn(name = "id_usuario_validador")
    private Usuario usuarioValidador;

    @Column(length = 20)
    private String tipo;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(columnDefinition = "TEXT")
    private String motivo;

    @Column(length = 20)
    private String estado = "PENDIENTE";

    private LocalDateTime fecha = LocalDateTime.now();
}
