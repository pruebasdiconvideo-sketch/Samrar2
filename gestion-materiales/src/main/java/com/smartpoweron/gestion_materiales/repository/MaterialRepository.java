package com.smartpoweron.gestion_materiales.repository;

import com.smartpoweron.gestion_materiales.model.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
    Optional<Material> findByCodigo(String codigo);
    Optional<Material> findByCodigoAndProyectoAsignado(String codigo, String proyectoAsignado);
    boolean existsByCodigoAndProyectoAsignado(String codigo, String proyectoAsignado);
    List<Material> findByCategoria(String categoria);

    @Query("SELECT m FROM Material m WHERE m.stockActual <= m.stockMinimo")
    List<Material> findMaterialesEnAlertaStock();
}
