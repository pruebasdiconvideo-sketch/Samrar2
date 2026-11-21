package com.smartpoweron.gestion_materiales.service;

import com.smartpoweron.gestion_materiales.model.*;
import com.smartpoweron.gestion_materiales.repository.TrazabilidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrazabilidadService {

    @Autowired
    private TrazabilidadRepository trazabilidadRepository;

    public void registrarMovimiento(Material material, Usuario usuario, Proyecto proyecto, OrdenCompra oc, String tipo, int cantidad) {
        Trazabilidad t = new Trazabilidad();
        t.setMaterial(material);
        t.setUsuario(usuario);
        t.setProyecto(proyecto);
        t.setOrdenCompra(oc);
        t.setTipoMovimiento(tipo);
        t.setCantidad(cantidad);
        trazabilidadRepository.save(t);
    }

    public List<Trazabilidad> consultarPorMaterial(String codigoMaterial) {
        if (codigoMaterial == null || codigoMaterial.trim().isEmpty()) {
            return trazabilidadRepository.findAll();
        }
        return trazabilidadRepository.findByMaterialCodigoOrderByFechaMovimientoDesc(codigoMaterial.trim());
    }

    public Trazabilidad obtenerPorId(Long id) {
        return trazabilidadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado (id=" + id + ")."));
    }

    /**
     * Edición de un movimiento de trazabilidad. Permite corregir el tipo de
     * movimiento y la cantidad.
     *
     * <p><strong>Advertencia:</strong> esta edición NO recalcula el stock del
     * material; usar solo para corregir errores de registro.</p>
     */
    @Transactional
    public Trazabilidad actualizarTrazabilidad(Long id, String tipoMovimiento, Integer cantidad) {
        Trazabilidad t = obtenerPorId(id);
        if (tipoMovimiento != null && !tipoMovimiento.isBlank()) {
            t.setTipoMovimiento(tipoMovimiento);
        }
        if (cantidad != null) {
            t.setCantidad(cantidad);
        }
        return trazabilidadRepository.save(t);
    }

    @Transactional
    public void eliminarTrazabilidad(Long id) {
        Trazabilidad t = obtenerPorId(id);
        try {
            trazabilidadRepository.delete(t);
            trazabilidadRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("No se puede eliminar el movimiento: tiene registros dependientes.");
        }
    }
}
