package com.smartpoweron.gestion_materiales.service;

import com.smartpoweron.gestion_materiales.model.*;
import com.smartpoweron.gestion_materiales.repository.MaterialRepository;
import com.smartpoweron.gestion_materiales.repository.ProyectoRepository;
import com.smartpoweron.gestion_materiales.repository.SolicitudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class SolicitudService {

    @Autowired private SolicitudRepository solicitudRepository;
    @Autowired private ProyectoRepository proyectoRepository;
    @Autowired private MaterialRepository materialRepository;

    public List<Solicitud> listarTodas() {
        return solicitudRepository.findAll();
    }

    public List<Solicitud> listarRetenidas() {
        return solicitudRepository.findByEstadoOrderByFechaSolicitudAsc("RETENIDA");
    }

    public List<Solicitud> listarListasParaDespacho() {
        return solicitudRepository.findByEstadoOrderByFechaSolicitudAsc("LISTA_DESPACHO");
    }

    @Transactional
    public Solicitud crearYEnviarSolicitud(Long idProyecto, List<Long> idsMateriales, List<Integer> cantidades, Usuario solicitante) {
        Proyecto proyecto = proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado."));

        if ("CERRADO".equals(proyecto.getEstado())) {
            throw new RuntimeException("No se pueden crear solicitudes para un proyecto CERRADO.");
        }
        if (idsMateriales == null || idsMateriales.isEmpty()) {
            throw new RuntimeException("Debe incluir al menos un material en la solicitud.");
        }

        Solicitud solicitud = new Solicitud();
        solicitud.setProyecto(proyecto);
        solicitud.setUsuarioSolicitante(solicitante);
        solicitud.setCostoTotal(BigDecimal.ZERO);
        solicitud.setEstado("LISTA_DESPACHO");

        List<DetalleSolicitud> detalles = new ArrayList<>();

        for (int i = 0; i < idsMateriales.size(); i++) {
            int cant = cantidades.get(i);
            if (cant <= 0) {
                throw new RuntimeException("La cantidad solicitada debe ser mayor a cero.");
            }
            Material mat = materialRepository.findById(idsMateriales.get(i))
                    .orElseThrow(() -> new RuntimeException("Material no encontrado."));

            DetalleSolicitud det = new DetalleSolicitud();
            det.setSolicitud(solicitud);
            det.setMaterial(mat);
            det.setCantidadSolicitada(cant);
            det.setCantidadDespachada(0);
            detalles.add(det);
        }

        solicitud.setDetalles(detalles);
        return solicitudRepository.save(solicitud);
    }

    public Solicitud aprobarSolicitudRetenida(Long idSolicitud, Usuario admin) {
        Solicitud sol = solicitudRepository.findById(idSolicitud)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada."));
        sol.setEstado("LISTA_DESPACHO");
        return solicitudRepository.save(sol);
    }

    public Solicitud rechazarSolicitudRetenida(Long idSolicitud, Usuario admin) {
        Solicitud sol = solicitudRepository.findById(idSolicitud)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada."));
        sol.setEstado("RECHAZADA");
        return solicitudRepository.save(sol);
    }

    public Solicitud obtenerPorId(Long id) {
        return solicitudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada (id=" + id + ")."));
    }

    /**
     * Edición administrativa de una solicitud: permite ajustar el estado y el
     * costo total. El proyecto, el solicitante y el detalle de ítems no se
     * modifican desde aquí (se definen al crear la OTI).
     */
    @Transactional
    public Solicitud actualizarSolicitud(Long id, String estado, BigDecimal costoTotal) {
        Solicitud s = obtenerPorId(id);
        if (estado != null && !estado.isBlank()) {
            s.setEstado(estado);
        }
        if (costoTotal != null) {
            s.setCostoTotal(costoTotal);
        }
        return solicitudRepository.save(s);
    }

    @Transactional
    public void eliminarSolicitud(Long id) {
        Solicitud s = obtenerPorId(id);
        try {
            solicitudRepository.delete(s);
            solicitudRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException(
                "No se puede eliminar: la solicitud tiene despachos asociados. "
                + "Anule o elimine primero los despachos relacionados.");
        }
    }
}
