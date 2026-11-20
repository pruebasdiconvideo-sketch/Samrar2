package com.smartpoweron.gestion_materiales.repository;

import com.smartpoweron.gestion_materiales.model.DetalleSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleSolicitudRepository extends JpaRepository<DetalleSolicitud, Long> {
}
