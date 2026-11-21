package com.smartpoweron.gestion_materiales.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "entrega")
public class Entrega {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_entrega")
    private Long idEntrega;

    @OneToOne
    @JoinColumn(name = "id_despacho", unique = true, nullable = false)
    private Despacho despacho;

    @ManyToOne
    @JoinColumn(name = "id_usuario_operario", nullable = false)
    private Usuario usuarioOperario;

    @Column(name = "fecha_confirmacion")
    private LocalDateTime fechaConfirmacion = LocalDateTime.now();

    @Column(nullable = false)
    private Boolean confirmado = true;
}
