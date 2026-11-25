package com.smartpoweron.gestion_materiales.repository;

import com.smartpoweron.gestion_materiales.model.Despacho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DespachoRepository extends JpaRepository<Despacho, Long> {
    List<Despacho> findByEstadoOrderByFechaDespachoAsc(String estado);
    List<Despacho> findBySolicitudIdSolicitud(Long idSolicitud);
}
