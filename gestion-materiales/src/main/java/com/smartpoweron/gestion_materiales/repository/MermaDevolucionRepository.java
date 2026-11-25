package com.smartpoweron.gestion_materiales.repository;

import com.smartpoweron.gestion_materiales.model.MermaDevolucion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MermaDevolucionRepository extends JpaRepository<MermaDevolucion, Long> {
    List<MermaDevolucion> findByTipo(String tipo);
    List<MermaDevolucion> findByTipoAndEstado(String tipo, String estado);
    List<MermaDevolucion> findByTipoAndProyectoIdProyecto(String tipo, Long idProyecto);
    List<MermaDevolucion> findByTipoAndFechaBetween(String tipo, LocalDateTime inicio, LocalDateTime fin);
}
