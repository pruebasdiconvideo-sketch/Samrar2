package com.smartpoweron.gestion_materiales.controller;

import com.smartpoweron.gestion_materiales.model.*;
import com.smartpoweron.gestion_materiales.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiRestController {

    @Autowired private AuthService authService;
    @Autowired private UsuarioService usuarioService;
    @Autowired private MaterialService materialService;
    @Autowired private ProyectoService proyectoService;
    @Autowired private SolicitudService solicitudService;
    @Autowired private DespachoService despachoService;
    @Autowired private CalidadService calidadService;
    @Autowired private MermaService mermaService;
    @Autowired private TrazabilidadService trazabilidadService;

    private Usuario getUsuarioActual(Authentication auth) {
        return authService.obtenerUsuarioActual(auth.getName());
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        try {
            return ResponseEntity.ok(authService.autenticar(body.get("correo"), body.get("password")));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas o usuario inactivo"));
        }
    }

    @GetMapping("/admin/usuarios")
    public List<Usuario> listarUsuarios() {
        return usuarioService.listarUsuarios();
    }

    @PostMapping("/admin/usuarios")
    public ResponseEntity<?> crearUsuario(@RequestBody Map<String, Object> body) {
        try {
            Usuario u = usuarioService.crearUsuario(
                body.get("nombreCompleto").toString(),
                body.get("correo").toString(),
                body.get("password").toString(),
                Long.valueOf(body.get("idRol").toString())
            );
            return ResponseEntity.ok(u);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/admin/usuarios/{id}/estado")
    public ResponseEntity<?> cambiarEstadoUsuario(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        return ResponseEntity.ok(usuarioService.cambiarEstado(id, body.get("activo")));
    }

    @PutMapping("/admin/usuarios/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            Long idRol = body.get("idRol") != null ? Long.valueOf(body.get("idRol").toString()) : null;
            Boolean activo = body.get("activo") != null ? Boolean.valueOf(body.get("activo").toString()) : null;
            Usuario u = usuarioService.actualizarUsuario(
                id,
                body.get("nombreCompleto").toString(),
                body.get("correo").toString(),
                idRol,
                activo
            );
            return ResponseEntity.ok(Map.of("ok", true, "id", u.getIdUsuario()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/admin/usuarios/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        try {
            usuarioService.eliminarUsuario(id);
            return ResponseEntity.ok(Map.of("ok", true, "id", id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/admin/parametros-prueba")
    public List<ParametroPrueba> listarParametros() {
        return calidadService.listarParametros();
    }

    @PostMapping("/admin/parametros-prueba")
    public ResponseEntity<?> crearParametro(@RequestBody Map<String, Object> body) {
        try {
            ParametroPrueba p = calidadService.crearParametro(
                body.get("tipoPrueba") != null ? body.get("tipoPrueba").toString() : null,
                body.get("unidad") != null ? body.get("unidad").toString() : null,
                new BigDecimal(body.get("valorMinimo").toString()),
                new BigDecimal(body.get("valorMaximo").toString()),
                body.get("normaReferencia") != null ? body.get("normaReferencia").toString() : null
            );
            return ResponseEntity.ok(Map.of("ok", true, "id", p.getIdParametro()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/admin/parametros-prueba/{id}")
    public ResponseEntity<?> actualizarParametro(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            ParametroPrueba p = calidadService.actualizarParametro(
                id,
                body.get("tipoPrueba") != null ? body.get("tipoPrueba").toString() : null,
                body.get("unidad") != null ? body.get("unidad").toString() : null,
                new BigDecimal(body.get("valorMinimo").toString()),
                new BigDecimal(body.get("valorMaximo").toString()),
                body.get("normaReferencia") != null ? body.get("normaReferencia").toString() : null
            );
            return ResponseEntity.ok(Map.of("ok", true, "id", p.getIdParametro()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/admin/parametros-prueba/{id}")
    public ResponseEntity<?> eliminarParametro(@PathVariable Long id) {
        try {
            calidadService.eliminarParametro(id);
            return ResponseEntity.ok(Map.of("ok", true, "id", id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/almacen/materiales")
    public List<Material> listarMateriales() {
        return materialService.listarMateriales();
    }

    @GetMapping("/almacen/materiales/alertas")
    public List<Material> listarAlertasStock() {
        return materialService.listarAlertasStock();
    }

    @PostMapping("/almacen/materiales")
    public ResponseEntity<?> registrarMaterial(@RequestBody Material material) {
        try {
            return ResponseEntity.ok(materialService.registrarMaterialCatalogo(material));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/almacen/materiales/{id}")
    public ResponseEntity<?> obtenerMaterial(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(materialService.obtenerPorId(id));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/almacen/materiales/{id}")
    public ResponseEntity<?> actualizarMaterial(@PathVariable Long id, @RequestBody Material material) {
        try {
            return ResponseEntity.ok(materialService.actualizarMaterial(id, material));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/almacen/materiales/{id}")
    public ResponseEntity<?> eliminarMaterial(@PathVariable Long id) {
        try {
            materialService.eliminarMaterial(id);
            return ResponseEntity.ok(Map.of("ok", true, "id", id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/almacen/ordenes-compra")
    public List<OrdenCompra> listarOrdenesAbiertas() {
        return materialService.listarOrdenesAbiertas();
    }

       @PostMapping("/almacen/ingresos")
    public ResponseEntity<?> registrarIngreso(@RequestBody Map<String, Object> body, Authentication auth) {
        try {
            Usuario u = authService.obtenerUsuarioActual(auth.getName());
            Material m = materialService.registrarIngresoFlexible(
                body.get("codigoOc").toString(),
                body.get("proveedor") != null ? body.get("proveedor").toString() : "Siemens / Proveedor",
                body.get("proyectoDestino").toString(),
                Long.valueOf(body.get("idMaterial").toString()),
                body.get("ubicacion") != null ? body.get("ubicacion").toString() : "",
                Integer.parseInt(body.get("cantidad").toString()),
                u
            );
            return ResponseEntity.ok(m);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/produccion/proyectos")
    public List<Proyecto> listarProyectos() {
        return proyectoService.listarProyectos();
    }

    @PostMapping("/produccion/proyectos")
    public ResponseEntity<?> crearProyecto(@RequestBody Proyecto proyecto) {
        try {
            return ResponseEntity.ok(proyectoService.crearProyecto(proyecto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/produccion/proyectos/{id}")
    public ResponseEntity<?> actualizarProyecto(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            Proyecto p = proyectoService.actualizarProyecto(
                id,
                body.get("codigo").toString(),
                body.get("cliente").toString(),
                LocalDate.parse(body.get("fechaInicio").toString()),
                body.get("fechaEntregaEstimada") != null && !body.get("fechaEntregaEstimada").toString().isBlank()
                    ? LocalDate.parse(body.get("fechaEntregaEstimada").toString()) : null,
                body.get("responsable") != null ? body.get("responsable").toString() : null,
                body.get("estado") != null ? body.get("estado").toString() : null
            );
            return ResponseEntity.ok(Map.of("ok", true, "id", p.getIdProyecto()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/produccion/proyectos/{id}")
    public ResponseEntity<?> eliminarProyecto(@PathVariable Long id) {
        try {
            proyectoService.eliminarProyecto(id);
            return ResponseEntity.ok(Map.of("ok", true, "id", id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/produccion/solicitudes")
    public List<Solicitud> listarSolicitudes() {
        return solicitudService.listarTodas();
    }

    @SuppressWarnings("unchecked")
    @PostMapping("/produccion/solicitudes")
    public ResponseEntity<?> crearSolicitud(@RequestBody Map<String, Object> body, Authentication auth) {
        try {
            Long idProyecto = Long.valueOf(body.get("idProyecto").toString());
            List<Integer> rawIds = (List<Integer>) body.get("idsMateriales");
            List<Long> idsMateriales = rawIds.stream().map(Long::valueOf).toList();
            List<Integer> cantidades = (List<Integer>) body.get("cantidades");

            Solicitud s = solicitudService.crearYEnviarSolicitud(idProyecto, idsMateriales, cantidades, getUsuarioActual(auth));
            return ResponseEntity.ok(s);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/produccion/solicitudes/{id}/aprobar")
    public ResponseEntity<?> aprobarSolicitud(@PathVariable Long id, Authentication auth) {
        try {
            return ResponseEntity.ok(solicitudService.aprobarSolicitudRetenida(id, getUsuarioActual(auth)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/produccion/solicitudes/{id}/rechazar")
    public ResponseEntity<?> rechazarSolicitud(@PathVariable Long id, Authentication auth) {
        try {
            return ResponseEntity.ok(solicitudService.rechazarSolicitudRetenida(id, getUsuarioActual(auth)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/produccion/solicitudes/{id}")
    public ResponseEntity<?> actualizarSolicitud(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            String estado = body.get("estado") != null ? body.get("estado").toString() : null;
            BigDecimal costoTotal = body.get("costoTotal") != null && !body.get("costoTotal").toString().isBlank()
                    ? new BigDecimal(body.get("costoTotal").toString()) : null;
            Solicitud s = solicitudService.actualizarSolicitud(id, estado, costoTotal);
            return ResponseEntity.ok(Map.of("ok", true, "id", s.getIdSolicitud()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/produccion/solicitudes/{id}")
    public ResponseEntity<?> eliminarSolicitud(@PathVariable Long id) {
        try {
            solicitudService.eliminarSolicitud(id);
            return ResponseEntity.ok(Map.of("ok", true, "id", id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/despacho/cola")
    public List<Solicitud> listarColaDespacho() {
        return despachoService.listarColaDespacho();
    }

    @SuppressWarnings("unchecked")
    @PostMapping("/despacho/registrar")
    public ResponseEntity<?> registrarDespacho(@RequestBody Map<String, Object> body, Authentication auth) {
        try {
            Long idSolicitud = Long.valueOf(body.get("idSolicitud").toString());
            List<Integer> rawDet = (List<Integer>) body.get("idsDetalleSolicitud");
            List<Long> idsDetalle = rawDet.stream().map(Long::valueOf).toList();
            List<String> codigos = (List<String>) body.get("codigosDigitados");
            List<Integer> cantidades = (List<Integer>) body.get("cantidadesEntregadas");

            Despacho d = despachoService.registrarDespacho(idSolicitud, idsDetalle, codigos, cantidades, getUsuarioActual(auth));
            return ResponseEntity.ok(d);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/despacho/pendientes-confirmacion")
    public List<Despacho> listarPendientesConfirmacion() {
        return despachoService.listarPendientesConfirmacion();
    }

    @PostMapping("/despacho/{id}/confirmar")
    public ResponseEntity<?> confirmarDespacho(@PathVariable Long id, Authentication auth) {
        try {
            return ResponseEntity.ok(despachoService.confirmarRecepcion(id, getUsuarioActual(auth)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/calidad/pruebas")
    public List<PruebaCalidad> listarPruebas(@RequestParam Long idProyecto) {
        return calidadService.listarPruebasPorProyecto(idProyecto);
    }

    @PostMapping("/calidad/pruebas")
    public ResponseEntity<?> registrarPrueba(@RequestBody Map<String, Object> body, Authentication auth) {
        try {
            PruebaCalidad p = calidadService.registrarPrueba(
                Long.valueOf(body.get("idProyecto").toString()),
                Long.valueOf(body.get("idParametro").toString()),
                new BigDecimal(body.get("valorMedido").toString()),
                body.get("observacion") != null ? body.get("observacion").toString() : "",
                getUsuarioActual(auth)
            );
            return ResponseEntity.ok(p);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    @PutMapping("/calidad/pruebas/{id}")
    public ResponseEntity<?> editarPrueba(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            PruebaCalidad p = calidadService.editarPrueba(
                id,
                new BigDecimal(body.get("valorMedido").toString()),
                body.get("observacion") != null ? body.get("observacion").toString() : ""
            );
            return ResponseEntity.ok(p);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/calidad/pruebas/{id}")
    public ResponseEntity<?> eliminarPrueba(@PathVariable Long id) {
        try {
            calidadService.eliminarPrueba(id);
            return ResponseEntity.ok(Map.of("ok", true, "id", id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/calidad/proyectos/{idProyecto}/certificado.pdf")
    public ResponseEntity<?> descargarCertificado(@PathVariable Long idProyecto) {
        try {
            byte[] pdf = calidadService.generarCertificadoPdf(idProyecto);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Certificado_FAT_Proyecto_" + idProyecto + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/merma/devoluciones")
    public ResponseEntity<?> registrarMermaDevolucion(@RequestBody Map<String, Object> body, Authentication auth) {
        try {
            MermaDevolucion md = mermaService.registrar(
                Long.valueOf(body.get("idProyecto").toString()),
                Long.valueOf(body.get("idMaterial").toString()),
                body.get("tipo").toString(),
                Integer.parseInt(body.get("cantidad").toString()),
                body.get("motivo") != null ? body.get("motivo").toString() : "",
                getUsuarioActual(auth)
            );
            return ResponseEntity.ok(md);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/merma/devoluciones/{id}/reintegrar")
    public ResponseEntity<?> reintegrar(@PathVariable Long id, Authentication auth) {
        try {
            return ResponseEntity.ok(mermaService.reintegrarDevolucion(id, getUsuarioActual(auth)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/merma/devoluciones/{id}/marcar-merma")
    public ResponseEntity<?> marcarMerma(@PathVariable Long id, Authentication auth) {
        try {
            return ResponseEntity.ok(mermaService.confirmarMerma(id, getUsuarioActual(auth)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/merma/devoluciones/{id}/validar")
    public ResponseEntity<?> validarMerma(@PathVariable Long id, Authentication auth) {
        try {
            return ResponseEntity.ok(mermaService.confirmarMerma(id, getUsuarioActual(auth)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/merma/reporte")
    public List<MermaDevolucion> reporteMermas() {
        return mermaService.listarTodas();
    }

    @GetMapping("/trazabilidad/historial")
    public List<Trazabilidad> consultarTrazabilidad(@RequestParam(required = false) String codigo) {
        return trazabilidadService.consultarPorMaterial(codigo);
    }

    @PutMapping("/trazabilidad/{id}")
    public ResponseEntity<?> actualizarTrazabilidad(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            String tipo = body.get("tipoMovimiento") != null ? body.get("tipoMovimiento").toString() : null;
            Integer cantidad = body.get("cantidad") != null ? Integer.valueOf(body.get("cantidad").toString()) : null;
            Trazabilidad t = trazabilidadService.actualizarTrazabilidad(id, tipo, cantidad);
            return ResponseEntity.ok(Map.of("ok", true, "id", t.getIdTrazabilidad()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/trazabilidad/{id}")
    public ResponseEntity<?> eliminarTrazabilidad(@PathVariable Long id) {
        try {
            trazabilidadService.eliminarTrazabilidad(id);
            return ResponseEntity.ok(Map.of("ok", true, "id", id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
