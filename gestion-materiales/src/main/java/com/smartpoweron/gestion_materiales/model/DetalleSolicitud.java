package com.smartpoweron.gestion_materiales.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Data
@Entity
@Table(name = "detalle_solicitud")
public class DetalleSolicitud {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_solicitud")
    private Long idDetalleSolicitud;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_solicitud", nullable = false)
    @ToString.Exclude
    private Solicitud solicitud;

    @ManyToOne
    @JoinColumn(name = "id_material", nullable = false)
    private Material material;

    @Column(name = "cantidad_solicitada", nullable = false)
    private Integer cantidadSolicitada;

    @Column(name = "cantidad_despachada")
    private Integer cantidadDespachada = 0;
}
