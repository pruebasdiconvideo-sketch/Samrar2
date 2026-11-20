package com.smartpoweron.gestion_materiales.service;

import com.smartpoweron.gestion_materiales.model.*;
import com.smartpoweron.gestion_materiales.repository.TrazabilidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
