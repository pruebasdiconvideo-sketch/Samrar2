package com.smartpoweron.gestion_materiales.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "detalle_despacho")
public class DetalleDespacho {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_despacho")
    private Long idDetalleDespacho;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_despacho", nullable = false)
    private Despacho despacho;

    @ManyToOne
    @JoinColumn(name = "id_detalle_solicitud", nullable = false)
    private DetalleSolicitud detalleSolicitud;

    @ManyToOne
    @JoinColumn(name = "id_material", nullable = false)
    private Material material;

    @Column(name = "codigo_digitado", length = 50)
    private String codigoDigitado;

    @Column(name = "cantidad_entregada", nullable = false)
    private Integer cantidadEntregada;
}
