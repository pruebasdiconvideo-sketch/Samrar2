package com.smartpoweron.gestion_materiales.service;

import com.smartpoweron.gestion_materiales.model.*;
import com.smartpoweron.gestion_materiales.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DespachoService {

    @Autowired private SolicitudRepository solicitudRepository;
    @Autowired private DespachoRepository despachoRepository;
    @Autowired private EntregaRepository entregaRepository;
    @Autowired private MaterialRepository materialRepository;
    @Autowired private TrazabilidadService trazabilidadService;

    public List<Solicitud> listarColaDespacho() {
        return solicitudRepository.findByEstadoOrderByFechaSolicitudAsc("LISTA_DESPACHO");
    }

    public List<Despacho> listarPendientesConfirmacion() {
        return despachoRepository.findByEstadoOrderByFechaDespachoAsc("PENDIENTE_CONFIRMACION");
    }

    public List<Despacho> listarTodosLosDespachos() {
        return despachoRepository.findAll(Sort.by(Sort.Direction.DESC, "fechaDespacho"));
    }

    public List<Entrega> listarEntregasConfirmadas() {
        return entregaRepository.findAll(Sort.by(Sort.Direction.DESC, "idEntrega"));
    }

    @Transactional
    public Despacho registrarDespacho(Long idSolicitud, List<Long> idsDetalleSol, List<String> codigosDigitados, List<Integer> cantidadesEntregar, Usuario almacenero) {
        Solicitud solicitud = solicitudRepository.findById(idSolicitud)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada."));

        if (!"LISTA_DESPACHO".equals(solicitud.getEstado())) {
            throw new RuntimeException("La solicitud no está habilitada para despacho.");
        }

        Despacho despacho = new Despacho();
        despacho.setSolicitud(solicitud);
        despacho.setUsuarioAlmacenero(almacenero);
        despacho.setEstado("PENDIENTE_CONFIRMACION");

        List<DetalleDespacho> detallesDespacho = new ArrayList<>();
        boolean todoCompleto = true;

        for (DetalleSolicitud detSol : solicitud.getDetalles()) {
            int idx = idsDetalleSol.indexOf(detSol.getIdDetalleSolicitud());
            int cantEntregar = (idx >= 0) ? cantidadesEntregar.get(idx) : 0;
            String codDigitado = (idx >= 0) ? codigosDigitados.get(idx) : "";

            int pendiente = detSol.getCantidadSolicitada() - detSol.getCantidadDespachada();

            if (cantEntregar > 0) {
                if (codDigitado == null || !detSol.getMaterial().getCodigo().equalsIgnoreCase(codDigitado.trim())) {
                    throw new RuntimeException("Código incorrecto para " + detSol.getMaterial().getNombre() + ". Esperado: " + detSol.getMaterial().getCodigo());
                }
                if (cantEntregar > pendiente) {
                    throw new RuntimeException("No puede entregar más de lo pendiente para " + detSol.getMaterial().getNombre());
                }
                if (detSol.getMaterial().getStockActual() < cantEntregar) {
                    throw new RuntimeException("Stock insuficiente en almacén para: " + detSol.getMaterial().getNombre());
                }

                DetalleDespacho dd = new DetalleDespacho();
                dd.setDespacho(despacho);
                dd.setDetalleSolicitud(detSol);
                dd.setMaterial(detSol.getMaterial());
                dd.setCodigoDigitado(codDigitado.trim());
                dd.setCantidadEntregada(cantEntregar);
                detallesDespacho.add(dd);
            }

            if ((detSol.getCantidadDespachada() + cantEntregar) < detSol.getCantidadSolicitada()) {
                todoCompleto = false;
            }
        }

        if (detallesDespacho.isEmpty()) {
            throw new RuntimeException("Debe entregar al menos un ítem en el despacho.");
        }

        despacho.setTipo(todoCompleto ? "COMPLETO" : "PARCIAL");
        despacho.setDetalles(detallesDespacho);
        Despacho guardado = despachoRepository.save(despacho);

        if (todoCompleto) {
            solicitud.setEstado("EN_TRANSITO");
            solicitudRepository.save(solicitud);
        }

        return guardado;
    }

    @Transactional
    public Entrega confirmarRecepcion(Long idDespacho, Usuario operario) {
        Despacho despacho = despachoRepository.findById(idDespacho)
                .orElseThrow(() -> new RuntimeException("Despacho no encontrado."));

        if ("CONFIRMADO".equals(despacho.getEstado()) || entregaRepository.existsByDespachoIdDespacho(idDespacho)) {
            throw new RuntimeException("Este despacho ya fue confirmado previamente.");
        }

        for (DetalleDespacho dd : despacho.getDetalles()) {
            Material material = dd.getMaterial();
            if (material.getStockActual() < dd.getCantidadEntregada()) {
                throw new RuntimeException("Stock insuficiente al confirmar: " + material.getNombre());
            }
            material.setStockActual(material.getStockActual() - dd.getCantidadEntregada());
            materialRepository.save(material);

            DetalleSolicitud detSol = dd.getDetalleSolicitud();
            detSol.setCantidadDespachada(detSol.getCantidadDespachada() + dd.getCantidadEntregada());

            trazabilidadService.registrarMovimiento(material, operario, despacho.getSolicitud().getProyecto(), null, "SALIDA", dd.getCantidadEntregada());
        }

        despacho.setEstado("CONFIRMADO");
        despachoRepository.save(despacho);

        Solicitud solicitud = despacho.getSolicitud();
        boolean solicitudCompleta = solicitud.getDetalles().stream()
                .allMatch(d -> d.getCantidadDespachada() >= d.getCantidadSolicitada());

        if (solicitudCompleta) {
            solicitud.setEstado("DESPACHADA");
            solicitudRepository.save(solicitud);
        }

        Entrega entrega = new Entrega();
        entrega.setDespacho(despacho);
        entrega.setUsuarioOperario(operario);
        entrega.setConfirmado(true);
        return entregaRepository.save(entrega);
    }
}
