package com.smartpoweron.gestion_materiales.service;

import com.smartpoweron.gestion_materiales.model.*;
import com.smartpoweron.gestion_materiales.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MermaService {

    @Autowired
    private MermaDevolucionRepository mermaRepository;

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private TrazabilidadService trazabilidadService;

    public List<MermaDevolucion> listarTodas() {
        return mermaRepository.findAll();
    }

    public List<MermaDevolucion> listarDevolucionesPendientes() {
        return mermaRepository.findByTipoAndEstado("DEVOLUCION", "PENDIENTE");
    }

    public List<MermaDevolucion> listarMermasPendientes() {
        return mermaRepository.findByTipoAndEstado("MERMA", "PENDIENTE");
    }

    public MermaDevolucion registrar(Long idProyecto, Long idMaterial, String tipo, int cantidad, String motivo, Usuario operario) {
        if (cantidad <= 0) {
            throw new RuntimeException("La cantidad debe ser mayor a cero.");
        }
        if ("MERMA".equals(tipo) && (motivo == null || motivo.trim().isEmpty())) {
            throw new RuntimeException("El motivo es obligatorio para registrar una MERMA.");
        }

        Proyecto proyecto = proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado."));
        Material material = materialRepository.findById(idMaterial)
                .orElseThrow(() -> new RuntimeException("Material no encontrado."));

        MermaDevolucion md = new MermaDevolucion();
        md.setProyecto(proyecto);
        md.setMaterial(material);
        md.setUsuarioRegistra(operario);
        md.setTipo(tipo);
        md.setCantidad(cantidad);
        md.setMotivo(motivo);
        md.setEstado("PENDIENTE");
        return mermaRepository.save(md);
    }

    @Transactional
    public MermaDevolucion reintegrarDevolucion(Long idMerma, Usuario almacenero) {
        MermaDevolucion md = mermaRepository.findById(idMerma)
                .orElseThrow(() -> new RuntimeException("Registro no encontrado."));

        if (!"PENDIENTE".equals(md.getEstado())) {
            throw new RuntimeException("Este registro ya fue procesado.");
        }

        Material material = md.getMaterial();
        material.setStockActual(material.getStockActual() + md.getCantidad());
        materialRepository.save(material);

        md.setEstado("REINTEGRADO");
        md.setUsuarioValidador(almacenero);
        trazabilidadService.registrarMovimiento(material, almacenero, md.getProyecto(), null, "DEVOLUCION", md.getCantidad());
        return mermaRepository.save(md);
    }

    @Transactional
    public MermaDevolucion confirmarMerma(Long idMerma, Usuario validador) {
        MermaDevolucion md = mermaRepository.findById(idMerma)
                .orElseThrow(() -> new RuntimeException("Registro no encontrado."));

        if (!"PENDIENTE".equals(md.getEstado())) {
            throw new RuntimeException("Este registro ya fue procesado.");
        }

        md.setTipo("MERMA");
        md.setEstado("MERMADO");
        md.setUsuarioValidador(validador);
        trazabilidadService.registrarMovimiento(md.getMaterial(), validador, md.getProyecto(), null, "MERMA", md.getCantidad());
        return mermaRepository.save(md);
    }
}
