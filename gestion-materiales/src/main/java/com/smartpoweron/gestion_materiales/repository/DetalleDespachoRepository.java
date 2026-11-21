package com.smartpoweron.gestion_materiales.repository;

import com.smartpoweron.gestion_materiales.model.DetalleDespacho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleDespachoRepository extends JpaRepository<DetalleDespacho, Long> {
}
