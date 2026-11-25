package com.smartpoweron.gestion_materiales;

import com.smartpoweron.gestion_materiales.model.*;
import com.smartpoweron.gestion_materiales.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Verificación de integración del CRUD (Editar / Ver Detalle / Eliminar) en los
 * seis módulos solicitados, contra H2 en memoria.
 *
 * Cubre: Proyectos, Solicitudes, Aprobaciones, Calidad/pruebas, Trazabilidad y
 * Usuarios. Comprueba (1) que cada página renderiza con la columna "Acciones" y
 * (2) que los endpoints PUT/DELETE operan sobre la base de datos.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ModulosCrudIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ProyectoRepository proyectoRepository;
    @Autowired private SolicitudRepository solicitudRepository;
    @Autowired private DetalleSolicitudRepository detalleSolicitudRepository;
    @Autowired private PruebaCalidadRepository pruebaRepository;
    @Autowired private ParametroPruebaRepository parametroRepository;
    @Autowired private TrazabilidadRepository trazabilidadRepository;
    @Autowired private MaterialRepository materialRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private RolRepository rolRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private static final String CORREO = "admin@test.com";
    private Usuario admin;
    private Proyecto proyecto;
    private Material material;
    private ParametroPrueba parametro;
    private PruebaCalidad prueba;
    private Trazabilidad trazabilidad;
    private Solicitud solicitud;

    @BeforeEach
    void sembrar() {
        // Limpieza respetando FKs
        trazabilidadRepository.deleteAllInBatch();
        pruebaRepository.deleteAllInBatch();
        detalleSolicitudRepository.deleteAllInBatch();
        solicitudRepository.deleteAllInBatch();
        materialRepository.deleteAllInBatch();
        usuarioRepository.deleteAllInBatch();
        proyectoRepository.deleteAllInBatch();
        parametroRepository.deleteAllInBatch();
        rolRepository.deleteAllInBatch();

        Rol rolAdmin = new Rol(); rolAdmin.setNombre("ADMIN"); rolAdmin.setDescripcion("Admin"); rolRepository.save(rolAdmin);
        Rol rolJefe = new Rol(); rolJefe.setNombre("JEFE_PRODUCCION"); rolJefe.setDescripcion("Jefe"); rolRepository.save(rolJefe);
        Rol rolInsp = new Rol(); rolInsp.setNombre("INSPECTOR"); rolInsp.setDescripcion("Inspector"); rolRepository.save(rolInsp);

        admin = new Usuario();
        admin.setNombreCompleto("Admin Test");
        admin.setCorreo(CORREO);
        admin.setPasswordHash(passwordEncoder.encode("123456"));
        admin.setRol(rolAdmin);
        admin.setActivo(true);
        usuarioRepository.save(admin);

        proyecto = new Proyecto();
        proyecto.setCodigo("S02-TEST");
        proyecto.setCliente("Cliente Test");
        proyecto.setFechaInicio(java.time.LocalDate.of(2026, 1, 1));
        proyecto.setFechaEntregaEstimada(java.time.LocalDate.of(2026, 6, 1));
        proyecto.setResponsable("Resp Test");
        proyecto.setEstado("EN_PROCESO");
        proyectoRepository.save(proyecto);

        material = new Material();
        material.setCodigo("MAT-TEST");
        material.setNombre("Material Test");
        material.setEspecificacionTecnica("Espec test");
        material.setCategoria("Tableros");
        material.setProyectoAsignado("S02-TEST");
        material.setUbicacion("Pasillo 1");
        material.setPrecioUnitario(new BigDecimal("100.00"));
        material.setStockActual(10);
        material.setStockMinimo(1);
        materialRepository.save(material);

        parametro = new ParametroPrueba();
        parametro.setTipoPrueba("METALMECANICA");
        parametro.setUnidad("ud");
        parametro.setValorMinimo(BigDecimal.ZERO);
        parametro.setValorMaximo(BigDecimal.TEN);
        parametro.setNormaReferencia("IEC 61439");
        parametroRepository.save(parametro);

        prueba = new PruebaCalidad();
        prueba.setProyecto(proyecto);
        prueba.setParametro(parametro);
        prueba.setUsuarioInspector(admin);
        prueba.setValorMedido(BigDecimal.ONE);
        prueba.setResultado("CONFORME");
        prueba.setObservacion("[Tablero: =B00] Checklist OK");
        pruebaRepository.save(prueba);

        trazabilidad = new Trazabilidad();
        trazabilidad.setMaterial(material);
        trazabilidad.setUsuario(admin);
        trazabilidad.setProyecto(proyecto);
        trazabilidad.setTipoMovimiento("ENTRADA");
        trazabilidad.setCantidad(5);
        trazabilidadRepository.save(trazabilidad);

        solicitud = new Solicitud();
        solicitud.setProyecto(proyecto);
        solicitud.setUsuarioSolicitante(admin);
        solicitud.setEstado("LISTA_DESPACHO");
        solicitud.setCostoTotal(new BigDecimal("200.00"));
        DetalleSolicitud det = new DetalleSolicitud();
        det.setSolicitud(solicitud);
        det.setMaterial(material);
        det.setCantidadSolicitada(2);
        solicitud.setDetalles(List.of(det));
        solicitudRepository.save(solicitud);
    }

    // ===== Render de páginas (deben traer la columna "Acciones") =====

    @Test @WithMockUser(username = CORREO, roles = {"ADMIN"})
    void renderProyectos() throws Exception {
        mockMvc.perform(get("/produccion/proyectos"))
            .andExpect(status().isOk())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("Acciones")))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("abrirDetalleProyecto")))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("abrirEditarProyecto")))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("abrirEliminarProyecto")));
    }

    @Test @WithMockUser(username = CORREO, roles = {"ADMIN"})
    void renderSolicitudes() throws Exception {
        mockMvc.perform(get("/produccion/solicitudes"))
            .andExpect(status().isOk())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("abrirDetalleSolicitud")))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("abrirEliminarSolicitud")));
    }

    @Test @WithMockUser(username = CORREO, roles = {"ADMIN"})
    void renderAprobaciones() throws Exception {
        mockMvc.perform(get("/produccion/aprobaciones"))
            .andExpect(status().isOk())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("abrirDetalleSolicitud")));
    }

    @Test @WithMockUser(username = CORREO, roles = {"ADMIN"})
    void renderCalidad() throws Exception {
        mockMvc.perform(get("/calidad/pruebas"))
            .andExpect(status().isOk())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("abrirDetallePrueba")))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("abrirEliminarPrueba")))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("spo-tabs")))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("resumenPruebas")));
    }

    @Test @WithMockUser(username = CORREO, roles = {"ADMIN"})
    void renderTrazabilidad() throws Exception {
        mockMvc.perform(get("/trazabilidad"))
            .andExpect(status().isOk())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("abrirDetalleTraz")))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("abrirEditarTraz")))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("abrirEliminarTraz")));
    }

    @Test @WithMockUser(username = CORREO, roles = {"ADMIN"})
    void renderUsuarios() throws Exception {
        mockMvc.perform(get("/admin/usuarios"))
            .andExpect(status().isOk())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("abrirDetalleUsuario")))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("abrirEditarUsuario")))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("abrirEliminarUsuario")))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("abrirDetalleParametro")))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("abrirEliminarParametro")));
    }

    // ===== CRUD REST =====

    @Test @WithMockUser(username = CORREO, roles = {"ADMIN"})
    void crudProyecto() throws Exception {
        Long id = proyecto.getIdProyecto();
        String body = """
            {"codigo":"S02-TEST","cliente":"Cliente Editado","fechaInicio":"2026-01-01",
             "fechaEntregaEstimada":"2026-07-01","responsable":"Resp Nuevo","estado":"CERRADO"}
            """;
        mockMvc.perform(put("/api/produccion/proyectos/" + id).contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isOk()).andExpect(jsonPath("$.ok").value(true));
        assert "Cliente Editado".equals(proyectoRepository.findById(id).orElseThrow().getCliente());

        // El proyecto tiene solicitudes, pruebas y trazabilidad asociadas → el borrado
        // se rechaza con un mensaje claro (protección de integridad referencial).
        mockMvc.perform(delete("/api/produccion/proyectos/" + id))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.containsString("No se puede eliminar")));
    }

    @Test @WithMockUser(username = CORREO, roles = {"ADMIN"})
    void crudSolicitud() throws Exception {
        Long id = solicitud.getIdSolicitud();
        mockMvc.perform(put("/api/produccion/solicitudes/" + id)
                .contentType(MediaType.APPLICATION_JSON).content("{\"estado\":\"ENTREGADA\",\"costoTotal\":250.00}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.ok").value(true));
        assert "ENTREGADA".equals(solicitudRepository.findById(id).orElseThrow().getEstado());

        mockMvc.perform(delete("/api/produccion/solicitudes/" + id))
            .andExpect(status().isOk()).andExpect(jsonPath("$.ok").value(true));
        assert solicitudRepository.findById(id).isEmpty();
    }

    @Test @WithMockUser(username = CORREO, roles = {"ADMIN"})
    void crudPruebaCalidad() throws Exception {
        Long id = prueba.getIdPrueba();
        mockMvc.perform(delete("/api/calidad/pruebas/" + id))
            .andExpect(status().isOk()).andExpect(jsonPath("$.ok").value(true));
        assert pruebaRepository.findById(id).isEmpty();
    }

    @Test @WithMockUser(username = CORREO, roles = {"ADMIN"})
    void crudTrazabilidad() throws Exception {
        Long id = trazabilidad.getIdTrazabilidad();
        mockMvc.perform(put("/api/trazabilidad/" + id)
                .contentType(MediaType.APPLICATION_JSON).content("{\"tipoMovimiento\":\"SALIDA\",\"cantidad\":3}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.ok").value(true));
        Trazabilidad t = trazabilidadRepository.findById(id).orElseThrow();
        assert "SALIDA".equals(t.getTipoMovimiento());
        assert t.getCantidad() == 3;

        mockMvc.perform(delete("/api/trazabilidad/" + id))
            .andExpect(status().isOk()).andExpect(jsonPath("$.ok").value(true));
        assert trazabilidadRepository.findById(id).isEmpty();
    }

    @Test @WithMockUser(username = CORREO, roles = {"ADMIN"})
    void crudUsuario() throws Exception {
        Long id = admin.getIdUsuario();
        // Sin idRol → el servicio mantiene el rol actual; solo cambia nombre y estado.
        mockMvc.perform(put("/api/admin/usuarios/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombreCompleto\":\"Admin Editado\",\"correo\":\"" + CORREO + "\",\"activo\":true}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.ok").value(true));
        assert "Admin Editado".equals(usuarioRepository.findById(id).orElseThrow().getNombreCompleto());
    }

    @Test @WithMockUser(username = CORREO, roles = {"ADMIN"})
    void crudParametro() throws Exception {
        Long id = parametro.getIdParametro();
        mockMvc.perform(put("/api/admin/parametros-prueba/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tipoPrueba\":\"METALMECANICA\",\"unidad\":\"ud\",\"valorMinimo\":0,\"valorMaximo\":20,\"normaReferencia\":\"IEC 61439-2\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.ok").value(true));
        ParametroPrueba p = parametroRepository.findById(id).orElseThrow();
        assert "IEC 61439-2".equals(p.getNormaReferencia());

        // El parámetro tiene la inspección de calidad asociada → borrado rechazado (FK).
        mockMvc.perform(delete("/api/admin/parametros-prueba/" + id))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.containsString("No se puede eliminar")));
    }

    @Test @WithMockUser(username = CORREO, roles = {"ADMIN"})
    void crearParametro() throws Exception {
        String body = "{\"tipoPrueba\":\"DIELECTRICA\",\"unidad\":\"kV\",\"valorMinimo\":0,\"valorMaximo\":35,\"normaReferencia\":\"IEC 60664\"}";
        mockMvc.perform(post("/api/admin/parametros-prueba").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isOk()).andExpect(jsonPath("$.ok").value(true));
        ParametroPrueba creado = parametroRepository.findAll().stream()
            .filter(p -> "DIELECTRICA".equals(p.getTipoPrueba())).findFirst().orElseThrow();
        assert "kV".equals(creado.getUnidad());
        assert "IEC 60664".equals(creado.getNormaReferencia());

        // Sin dependientes → el borrado sí procede (complementa crudParametro, que prueba el rechazo por FK).
        mockMvc.perform(delete("/api/admin/parametros-prueba/" + creado.getIdParametro()))
            .andExpect(status().isOk()).andExpect(jsonPath("$.ok").value(true));
        assert parametroRepository.findById(creado.getIdParametro()).isEmpty();
    }
}
