package com.smartpoweron.gestion_materiales.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Aporta variables globales a todas las plantillas Thymeleaf.
 *
 * <p>Thymeleaf 3.1 (incluido en Spring Boot 3.2.x) eliminó del contexto de
 * expresiones los objetos {@code #httpServletRequest}, {@code #request},
 * {@code #session}, {@code #response} y {@code #servletContext}. La forma
 * recomendada de exponer datos del request a las vistas es inyectarlos como
 * atributos del modelo mediante un {@code @ControllerAdvice}.</p>
 *
 * <p>Este advice publica {@code requestURI} para que el fragmento
 * {@code fragments/navbar} pueda resaltar la opción de menú activa sin
 * depender de las expresiones obsoletas.</p>
 */
@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute("requestURI")
    public String requestURI(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
