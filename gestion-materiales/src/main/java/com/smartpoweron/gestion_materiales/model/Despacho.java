package com.smartpoweron.gestion_materiales.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "despacho")
public class Despacho {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_despacho")
    private Long idDespacho;

    @ManyToOne
    @JoinColumn(name = "id_solicitud", nullable = false)
    private Solicitud solicitud;

    @ManyToOne
    @JoinColumn(name = "id_usuario_almacenero", nullable = false)
    private Usuario usuarioAlmacenero;

    @Column(name = "fecha_despacho")
    private LocalDateTime fechaDespacho;

    @Column(length = 20)
    private String tipo;

    @Column(length = 30)
    private String estado = "PENDIENTE_CONFIRMACION";

    @OneToMany(mappedBy = "despacho", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleDespacho> detalles;

    @PrePersist
    protected void onCreate() {
        if (this.fechaDespacho == null) {
            this.fechaDespacho = LocalDateTime.now();
        }
    }
}
