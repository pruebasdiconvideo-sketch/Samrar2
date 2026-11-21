package com.smartpoweron.gestion_materiales;

import com.smartpoweron.gestion_materiales.model.Material;
import com.smartpoweron.gestion_materiales.model.Rol;
import com.smartpoweron.gestion_materiales.model.Usuario;
import com.smartpoweron.gestion_materiales.repository.DetalleSolicitudRepository;
import com.smartpoweron.gestion_materiales.repository.MaterialRepository;
import com.smartpoweron.gestion_materiales.repository.ParametroPruebaRepository;
import com.smartpoweron.gestion_materiales.repository.PruebaCalidadRepository;
import com.smartpoweron.gestion_materiales.repository.ProyectoRepository;
import com.smartpoweron.gestion_materiales.repository.RolRepository;
import com.smartpoweron.gestion_materiales.repository.SolicitudRepository;
import com.smartpoweron.gestion_materiales.repository.TrazabilidadRepository;
import com.smartpoweron.gestion_materiales.repository.UsuarioRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Verificación de integración del CRUD de Materiales (Editar / Ver Detalle / Eliminar)
 * contra H2 en memoria, sin tocar MySQL.
 *
 * <p>Comprueba dos cosas:</p>
 * <ol>
 *   <li>Que la página {@code /almacen/catalogo} renderiza con Thymeleaf y contiene
 *       la columna "Acciones" y los botones Ver Detalle / Editar / Eliminar.</li>
 *   <li>Que los endpoints REST GET/PUT/DELETE {@code /api/almacen/materiales/{id}}
 *       responden 200 y operan sobre la base de datos.</li>
 * </ol>
 */
@SpringBootTest
@AutoConfigureMockMvc
class CatalogoCrudIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private MaterialRepository materialRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private RolRepository rolRepository;
    @Autowired private TrazabilidadRepository trazabilidadRepository;
    @Autowired private PruebaCalidadRepository pruebaCalidadRepository;
    @Autowired private DetalleSolicitudRepository detalleSolicitudRepository;
    @Autowired private SolicitudRepository solicitudRepository;
    @Autowired private ProyectoRepository proyectoRepository;
    @Autowired private ParametroPruebaRepository parametroPruebaRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private static final String CORREO_ADMIN = "admin@test.com";

    @BeforeEach
    void sembrarDatos() {
        // Limpieza completa (respeta FKs) para no interferir con otros tests del mismo contexto
        trazabilidadRepository.deleteAllInBatch();
        pruebaCalidadRepository.deleteAllInBatch();
        detalleSolicitudRepository.deleteAllInBatch();
        solicitudRepository.deleteAllInBatch();
        materialRepository.deleteAllInBatch();
        usuarioRepository.deleteAllInBatch();
        proyectoRepository.deleteAllInBatch();
        parametroPruebaRepository.deleteAllInBatch();
        rolRepository.deleteAllInBatch();

        Rol admin = new Rol();
        admin.setNombre("ADMIN");
        admin.setDescripcion("Administrador");
        rolRepository.save(admin);

        Usuario u = new Usuario();
        u.setNombreCompleto("Admin Test");
        u.setCorreo(CORREO_ADMIN);
        u.setPasswordHash(passwordEncoder.encode("123456"));
        u.setRol(admin);
        u.setActivo(true);
        usuarioRepository.save(u);

        Material m = new Material();
        m.setCodigo("TEST-001");
        m.setNombre("Tablero Principal FAT");
        m.setEspecificacionTecnica("Tablero SIPROTEC 5, 380V");
        m.setCategoria("Tableros");
        m.setProyectoAsignado("S02-LIMA-NTE");
        m.setUbicacion("Pasillo 1 - Anaquel 2");
        m.setPrecioUnitario(new BigDecimal("1500.00"));
        m.setStockActual(3);
        m.setStockMinimo(1);
        materialRepository.save(m);
    }

    // --- Render de la página ---
    @Test
    @WithMockUser(username = CORREO_ADMIN, roles = {"ADMIN"})
    void catalogoRenderizaConBotonesDeAccion() throws Exception {
        mockMvc.perform(get("/almacen/catalogo"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Acciones")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("abrirDetalle")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("abrirEditar")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("abrirEliminar")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("modalEditar")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("modalEliminar")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("btnConfirmarEliminar")));
    }

    // --- CRUD REST: GET por id (Ver Detalle) ---
    @Test
    @WithMockUser(username = CORREO_ADMIN, roles = {"ADMIN"})
    void obtenerMaterialPorId() throws Exception {
        Material m = materialRepository.findAll().get(0);
        mockMvc.perform(get("/api/almacen/materiales/" + m.getIdMaterial()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("TEST-001"))
                .andExpect(jsonPath("$.nombre").value("Tablero Principal FAT"));
    }

    // --- CRUD REST: PUT (Editar) ---
    @Test
    @WithMockUser(username = CORREO_ADMIN, roles = {"ADMIN"})
    void actualizarMaterial() throws Exception {
        Material m = materialRepository.findAll().get(0);
        String body = """
            {
              "codigo": "TEST-001",
              "nombre": "Tablero Principal FAT (editado)",
              "especificacionTecnica": "Tablero SIPROTEC 5, 380V",
              "categoria": "Tableros",
              "proyectoAsignado": "S02-LIMA-NTE",
              "ubicacion": "Pasillo 2 - Anaquel 5",
              "stockMinimo": 2,
              "precioUnitario": 1600.00
            }
            """;
        mockMvc.perform(put("/api/almacen/materiales/" + m.getIdMaterial())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Tablero Principal FAT (editado)"))
                .andExpect(jsonPath("$.ubicacion").value("Pasillo 2 - Anaquel 5"))
                .andExpect(jsonPath("$.stockMinimo").value(2));

        // Verifica que persistió en la BD
        Material recargado = materialRepository.findById(m.getIdMaterial()).orElseThrow();
        assert "Tablero Principal FAT (editado)".equals(recargado.getNombre());
        assert "Pasillo 2 - Anaquel 5".equals(recargado.getUbicacion());
    }

    // --- CRUD REST: DELETE (Eliminar) ---
    @Test
    @WithMockUser(username = CORREO_ADMIN, roles = {"ADMIN"})
    void eliminarMaterial() throws Exception {
        Material m = materialRepository.findAll().get(0);
        Long id = m.getIdMaterial();
        mockMvc.perform(delete("/api/almacen/materiales/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ok").value(true));
        assert materialRepository.findById(id).isEmpty();
    }
}
