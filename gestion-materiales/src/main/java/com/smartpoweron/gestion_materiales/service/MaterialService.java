package com.smartpoweron.gestion_materiales.service;

import com.smartpoweron.gestion_materiales.model.Material;
import com.smartpoweron.gestion_materiales.model.OrdenCompra;
import com.smartpoweron.gestion_materiales.model.Usuario;
import com.smartpoweron.gestion_materiales.repository.MaterialRepository;
import com.smartpoweron.gestion_materiales.repository.OrdenCompraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
}
