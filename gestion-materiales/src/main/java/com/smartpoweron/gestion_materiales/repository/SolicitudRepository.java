package com.smartpoweron.gestion_materiales.repository;

import com.smartpoweron.gestion_materiales.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
    List<Solicitud> findByEstadoOrderByFechaSolicitudAsc(String estado);
    List<Solicitud> findByProyectoIdProyecto(Long idProyecto);
}
