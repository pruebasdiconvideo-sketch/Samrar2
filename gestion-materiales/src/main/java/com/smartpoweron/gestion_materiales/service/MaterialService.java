package com.smartpoweron.gestion_materiales.service;

import com.smartpoweron.gestion_materiales.model.Material;
import com.smartpoweron.gestion_materiales.model.OrdenCompra;
import com.smartpoweron.gestion_materiales.model.Usuario;
import com.smartpoweron.gestion_materiales.repository.MaterialRepository;
import com.smartpoweron.gestion_materiales.repository.OrdenCompraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class MaterialService {

    @Autowired private MaterialRepository materialRepository;
    @Autowired private OrdenCompraRepository ordenCompraRepository;
    @Autowired private TrazabilidadService trazabilidadService;

    public List<Material> listarMateriales() {
        return materialRepository.findAll();
    }

    public List<Material> listarAlertasStock() {
        return materialRepository.findMaterialesEnAlertaStock();
    }

    public List<OrdenCompra> listarOrdenesAbiertas() {
        return ordenCompraRepository.findByEstado("ABIERTA");
    }

    public Material registrarMaterialCatalogo(Material material) {
        if (materialRepository.existsByCodigoAndProyectoAsignado(material.getCodigo(), material.getProyectoAsignado())) {
            throw new RuntimeException("Este código de material ya está registrado para el proyecto seleccionado.");
        }
        if (material.getPrecioUnitario() == null) {
            material.setPrecioUnitario(BigDecimal.ZERO);
        }
        material.setStockActual(0);
        material.setFechaIngreso(LocalDateTime.now());
        return materialRepository.save(material);
    }

    @Transactional
    public Material registrarIngresoFlexible(String codigoOc, String proveedor, String proyectoDestino,
                                             Long idMaterialBase, String ubicacion, int cantidad, Usuario almacenero) {
        if (cantidad <= 0) {
            throw new RuntimeException("La cantidad recibida debe ser mayor a cero.");
        }
        if (codigoOc == null || codigoOc.trim().isEmpty()) {
            throw new RuntimeException("Debe seleccionar o digitar un código de Orden de Compra.");
        }

        // 1. Buscar o crear la Orden de Compra automáticamente si fue digitada por el almacenero
        String ocLimpia = codigoOc.trim().toUpperCase();
        OrdenCompra oc = ordenCompraRepository.findByCodigo(ocLimpia).orElseGet(() -> {
            OrdenCompra nueva = new OrdenCompra();
            nueva.setCodigo(ocLimpia);
            nueva.setProveedor(proveedor != null && !proveedor.trim().isEmpty() ? proveedor.trim() : "Proveedor General");
            nueva.setFechaEmision(LocalDate.now());
            nueva.setEstado("ABIERTA");
            return ordenCompraRepository.save(nueva);
        });

        if (!"ABIERTA".equals(oc.getEstado())) {
            throw new RuntimeException("La Orden de Compra " + ocLimpia + " está CERRADA y no admite más ingresos.");
        }

        // 2. Obtener el equipo base seleccionado del catálogo
        Material base = materialRepository.findById(idMaterialBase)
                .orElseThrow(() -> new RuntimeException("Material base no encontrado en el catálogo."));

        // 3. Buscar si ese código ya existe en el Proyecto Destino; si no existe, crearlo en ese proyecto
        Material destino = materialRepository.findByCodigoAndProyectoAsignado(base.getCodigo(), proyectoDestino)
                .orElseGet(() -> {
                    Material copia = new Material();
                    copia.setCodigo(base.getCodigo());
                    copia.setNombre(base.getNombre());
                    copia.setEspecificacionTecnica(base.getEspecificacionTecnica());
                    copia.setCategoria(base.getCategoria());
                    copia.setProyectoAsignado(proyectoDestino);
                    copia.setUbicacion(ubicacion != null && !ubicacion.trim().isEmpty() ? ubicacion.trim() : "Por ubicar en almacén");
                    copia.setPrecioUnitario(BigDecimal.ZERO);
                    copia.setStockActual(0);
                    copia.setStockMinimo(base.getStockMinimo());
                    return copia;
                });

        if (ubicacion != null && !ubicacion.trim().isEmpty()) {
            destino.setUbicacion(ubicacion.trim());
        }
        destino.setStockActual(destino.getStockActual() + cantidad);
        destino.setFechaIngreso(LocalDateTime.now());
        Material guardado = materialRepository.save(destino);

        // 4. Registrar movimiento de ENTRADA en Trazabilidad
        trazabilidadService.registrarMovimiento(guardado, almacenero, null, oc, "ENTRADA", cantidad);
        return guardado;
    }

    /**
     * Devuelve un material por su id.
     */
    public Material obtenerPorId(Long id) {
        return materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material no encontrado (id=" + id + ")."));
    }

    /**
     * Actualiza los datos de catálogo de un material existente.
     *
     * <p>No modifica {@code stockActual} ni {@code fechaIngreso}: el stock se
     * mueve exclusivamente vía ingresos (OC) y despachos, y la fecha de ingreso
     * es la del primer alta. Solo se editan datos descriptivos y de ubicación.</p>
     */
    @Transactional
    public Material actualizarMaterial(Long id, Material datos) {
        Material m = obtenerPorId(id);

        // Validar unicidad de (codigo, proyectoAsignado) si alguno cambia.
        boolean cambiaCodigo = !Objects.equals(m.getCodigo(), datos.getCodigo());
        boolean cambiaProyecto = !Objects.equals(m.getProyectoAsignado(), datos.getProyectoAsignado());
        if ((cambiaCodigo || cambiaProyecto)
                && materialRepository.existsByCodigoAndProyectoAsignado(datos.getCodigo(), datos.getProyectoAsignado())) {
            throw new RuntimeException("Ese código ya existe para el proyecto destino seleccionado.");
        }

        m.setCodigo(datos.getCodigo());
        m.setNombre(datos.getNombre());
        m.setEspecificacionTecnica(datos.getEspecificacionTecnica());
        m.setCategoria(datos.getCategoria());
        m.setProyectoAsignado(datos.getProyectoAsignado());
        m.setUbicacion(datos.getUbicacion());
        m.setStockMinimo(datos.getStockMinimo() != null ? datos.getStockMinimo() : 0);
        if (datos.getPrecioUnitario() != null) {
            m.setPrecioUnitario(datos.getPrecioUnitario());
        }
        return materialRepository.save(m);
    }

    /**
     * Elimina un material del catálogo.
     *
     * <p>Fallará con un mensaje claro si el material tiene movimientos
     * asociados (trazabilidad, detalles de solicitud/despacho o mermas),
     * porque la BD mantiene integridad referencial.</p>
     */
    @Transactional
    public void eliminarMaterial(Long id) {
        Material m = obtenerPorId(id);
        try {
            materialRepository.delete(m);
            materialRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException(
                "No se puede eliminar: el material tiene movimientos asociados "
                + "(trazabilidad, solicitudes, despachos o mermas). "
                + "Considere ajustar el stock a cero en su lugar.");
        }
    }
}
