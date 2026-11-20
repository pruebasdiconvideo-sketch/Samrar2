package com.smartpoweron.gestion_materiales.service;

import com.smartpoweron.gestion_materiales.model.Proyecto;
import com.smartpoweron.gestion_materiales.repository.ProyectoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
