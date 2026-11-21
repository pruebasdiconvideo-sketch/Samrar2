package com.smartpoweron.gestion_materiales.repository;

import com.smartpoweron.gestion_materiales.model.Trazabilidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TrazabilidadRepository extends JpaRepository<Trazabilidad, Long> {
    List<Trazabilidad> findByMaterialCodigoOrderByFechaMovimientoDesc(String codigoMaterial);
}
