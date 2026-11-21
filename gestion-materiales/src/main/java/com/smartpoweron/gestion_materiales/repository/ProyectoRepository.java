package com.smartpoweron.gestion_materiales.repository;

import com.smartpoweron.gestion_materiales.model.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {
    Optional<Proyecto> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);
}
