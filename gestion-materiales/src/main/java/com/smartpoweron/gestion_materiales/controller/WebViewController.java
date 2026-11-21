package com.smartpoweron.gestion_materiales.controller;

import com.smartpoweron.gestion_materiales.model.Usuario;
import com.smartpoweron.gestion_materiales.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebViewController {

    @Autowired private AuthService authService;
    @Autowired private UsuarioService usuarioService;
    @Autowired private MaterialService materialService;
    @Autowired private ProyectoService proyectoService;
    @Autowired private SolicitudService solicitudService;
    @Autowired private DespachoService despachoService;
    @Autowired private CalidadService calidadService;
    @Autowired private MermaService mermaService;
    @Autowired private TrazabilidadService trazabilidadService;

    private void cargarDatosComunes(Model model, Authentication auth) {
        if (auth != null) {
            Usuario u = authService.obtenerUsuarioActual(auth.getName());
            model.addAttribute("usuarioActual", u);
            model.addAttribute("rolActual", u.getRol().getNombre());
        }
    }

    // V01: Login en español
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // V02: Dashboard Principal
    @GetMapping({"/", "/inicio"})
    public String inicio(Model model, Authentication auth) {
        cargarDatosComunes(model, auth);
        model.addAttribute("alertasStock", materialService.listarAlertasStock().size());
        model.addAttribute("solicitudesRetenidas", solicitudService.listarRetenidas().size());
        model.addAttribute("colaDespacho", despachoService.listarColaDespacho().size());
        return "inicio";
    }

    // V03: Catálogo de Materiales
    @GetMapping("/almacen/catalogo")
    public String catalogo(Model model, Authentication auth) {
        cargarDatosComunes(model, auth);
        model.addAttribute("materiales", materialService.listarMateriales());
        model.addAttribute("proyectos", proyectoService.listarProyectos());
        return "almacen/catalogo";
    }

        // V04: Ingreso de Materiales con Orden de Compra
    @GetMapping("/almacen/ingresos")
    public String ingresos(Model model, Authentication auth) {
        cargarDatosComunes(model, auth);
        model.addAttribute("materiales", materialService.listarMateriales());
        model.addAttribute("ordenesCompra", materialService.listarOrdenesAbiertas());
        model.addAttribute("proyectos", proyectoService.listarProyectos());
        return "almacen/ingresos";
    }


    // V05: Gestión de Proyectos
    @GetMapping("/produccion/proyectos")
    public String proyectos(Model model, Authentication auth) {
        cargarDatosComunes(model, auth);
        model.addAttribute("proyectos", proyectoService.listarProyectos());
        return "produccion/proyectos";
    }

    // V06: Solicitudes de Materiales
    @GetMapping("/produccion/solicitudes")
    public String solicitudes(Model model, Authentication auth) {
        cargarDatosComunes(model, auth);
        model.addAttribute("proyectos", proyectoService.listarProyectos());
        model.addAttribute("materiales", materialService.listarMateriales());
        model.addAttribute("solicitudes", solicitudService.listarTodas());
        return "produccion/solicitudes";
    }

    // V07: Aprobación de Solicitudes Retenidas (Admin)
    @GetMapping("/produccion/aprobaciones")
    public String aprobaciones(Model model, Authentication auth) {
        cargarDatosComunes(model, auth);
        model.addAttribute("retenidas", solicitudService.listarRetenidas());
        return "produccion/aprobaciones";
    }

       // V08 y V09: Cola y Registro de Despacho (Almacenero)
    @GetMapping("/despacho/cola")
    public String colaDespacho(Model model, Authentication auth) {
        cargarDatosComunes(model, auth);
        model.addAttribute("solicitudes", despachoService.listarColaDespacho());
        model.addAttribute("historialDespachos", despachoService.listarTodosLosDespachos());
        return "despacho/cola";
    }


       // V10: Confirmación de Entrega en Taller (Operario)
    @GetMapping("/despacho/confirmar")
    public String confirmarEntrega(Model model, Authentication auth) {
        cargarDatosComunes(model, auth);
        model.addAttribute("despachos", despachoService.listarPendientesConfirmacion());
        model.addAttribute("entregasConfirmadas", despachoService.listarEntregasConfirmadas());
        return "despacho/confirmar";
    }


    // V11: Pruebas de Calidad FAT
    @GetMapping("/calidad/pruebas")
    public String pruebasCalidad(Model model, Authentication auth) {
        cargarDatosComunes(model, auth);
        model.addAttribute("proyectos", proyectoService.listarProyectos());
        model.addAttribute("parametros", calidadService.listarParametros());
        return "calidad/pruebas";
    }

    // V12 y V13: Devoluciones, Mermas y Validación
    @GetMapping("/merma/gestion")
    public String gestionMerma(Model model, Authentication auth) {
        cargarDatosComunes(model, auth);
        model.addAttribute("proyectos", proyectoService.listarProyectos());
        model.addAttribute("materiales", materialService.listarMateriales());
        model.addAttribute("registros", mermaService.listarTodas());
        return "merma/gestion";
    }

    // V14: Trazabilidad Histórica
    @GetMapping("/trazabilidad")
    public String trazabilidad(Model model, Authentication auth) {
        cargarDatosComunes(model, auth);
        model.addAttribute("movimientos", trazabilidadService.consultarPorMaterial(null));
        return "trazabilidad/historial";
    }

    // V15 y V16: Administración de Usuarios y Parámetros de Prueba
    @GetMapping("/admin/usuarios")
    public String adminUsuarios(Model model, Authentication auth) {
        cargarDatosComunes(model, auth);
        model.addAttribute("usuarios", usuarioService.listarUsuarios());
        model.addAttribute("roles", usuarioService.listarRoles());
        model.addAttribute("parametros", calidadService.listarParametros());
        return "admin/usuarios";
    }
}
