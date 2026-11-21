package com.smartpoweron.gestion_materiales.service;

import com.smartpoweron.gestion_materiales.model.Proyecto;
import com.smartpoweron.gestion_materiales.repository.ProyectoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
public class ProyectoService {

    @Autowired
    private ProyectoRepository proyectoRepository;

    public List<Proyecto> listarProyectos() {
        return proyectoRepository.findAll();
    }

    public Proyecto crearProyecto(Proyecto proyecto) {
        if (proyectoRepository.existsByCodigo(proyecto.getCodigo())) {
            throw new RuntimeException("Ya existe un proyecto con ese código.");
        }
        if (proyecto.getFechaEntregaEstimada() != null && proyecto.getFechaInicio() != null
                && proyecto.getFechaEntregaEstimada().isBefore(proyecto.getFechaInicio())) {
            throw new RuntimeException("La fecha de entrega estimada no puede ser anterior a la fecha de inicio.");
        }
        proyecto.setEstado("EN_PROCESO");
        return proyectoRepository.save(proyecto);
    }

    public Proyecto obtenerPorId(Long id) {
        return proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado (id=" + id + ")."));
    }

    @Transactional
    public Proyecto actualizarProyecto(Long id, String codigo, String cliente, LocalDate fechaInicio,
                                       LocalDate fechaEntregaEstimada, String responsable, String estado) {
        Proyecto p = obtenerPorId(id);
        if (!Objects.equals(p.getCodigo(), codigo) && proyectoRepository.existsByCodigo(codigo)) {
            throw new RuntimeException("Ya existe otro proyecto con ese código.");
        }
        if (fechaEntregaEstimada != null && fechaInicio != null && fechaEntregaEstimada.isBefore(fechaInicio)) {
            throw new RuntimeException("La fecha de entrega estimada no puede ser anterior a la fecha de inicio.");
        }
        p.setCodigo(codigo);
        p.setCliente(cliente);
        p.setFechaInicio(fechaInicio);
        p.setFechaEntregaEstimada(fechaEntregaEstimada);
        p.setResponsable(responsable);
        if (estado != null && !estado.isBlank()) {
            p.setEstado(estado);
        }
        return proyectoRepository.save(p);
    }

    @Transactional
    public void eliminarProyecto(Long id) {
        Proyecto p = obtenerPorId(id);
        try {
            proyectoRepository.delete(p);
            proyectoRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException(
                "No se puede eliminar: el proyecto tiene solicitudes, pruebas de calidad, "
                + "materiales o movimientos de trazabilidad asociados.");
        }
    }
}
