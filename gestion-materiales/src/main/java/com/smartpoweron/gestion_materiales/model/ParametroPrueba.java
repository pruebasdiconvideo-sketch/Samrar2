package com.smartpoweron.gestion_materiales.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "parametro_prueba")
public class ParametroPrueba {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_parametro")
    private Long idParametro;

    @Column(name = "tipo_prueba", length = 50)
    private String tipoPrueba;

    @Column(length = 20)
    private String unidad;

    @Column(name = "valor_minimo", precision = 10, scale = 3)
    private BigDecimal valorMinimo;

    @Column(name = "valor_maximo", precision = 10, scale = 3)
    private BigDecimal valorMaximo;

    @Column(name = "norma_referencia", length = 150)
    private String normaReferencia;
}
