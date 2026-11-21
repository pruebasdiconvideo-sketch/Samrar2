package com.smartpoweron.gestion_materiales.repository;

import com.smartpoweron.gestion_materiales.model.OrdenCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrdenCompraRepository extends JpaRepository<OrdenCompra, Long> {
    Optional<OrdenCompra> findByCodigo(String codigo);
    List<OrdenCompra> findByEstado(String estado);
}
