package com.smartpoweron.gestion_materiales.repository;

import com.smartpoweron.gestion_materiales.model.Entrega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EntregaRepository extends JpaRepository<Entrega, Long> {
    boolean existsByDespachoIdDespacho(Long idDespacho);
    Optional<Entrega> findByDespachoIdDespacho(Long idDespacho);
}
