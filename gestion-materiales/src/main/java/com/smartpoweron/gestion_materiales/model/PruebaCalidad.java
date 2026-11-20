package com.smartpoweron.gestion_materiales.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "prueba_calidad")
public class PruebaCalidad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prueba")
    private Long idPrueba;

    @ManyToOne
    @JoinColumn(name = "id_proyecto", nullable = false)
    private Proyecto proyecto;

    @ManyToOne
    @JoinColumn(name = "id_parametro", nullable = false)
    private ParametroPrueba parametro;

    @ManyToOne
    @JoinColumn(name = "id_usuario_inspector", nullable = false)
    private Usuario usuarioInspector;

    @Column(name = "valor_medido", precision = 10, scale = 3)
    private BigDecimal valorMedido;

    @Column(length = 20)
    private String resultado;

    @Column(columnDefinition = "TEXT")
    private String observacion;

    @Column(name = "fecha_prueba")
    private LocalDateTime fechaPrueba = LocalDateTime.now();
}
