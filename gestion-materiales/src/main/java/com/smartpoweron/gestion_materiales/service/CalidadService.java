package com.smartpoweron.gestion_materiales.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.smartpoweron.gestion_materiales.model.*;
import com.smartpoweron.gestion_materiales.repository.ParametroPruebaRepository;
import com.smartpoweron.gestion_materiales.repository.ProyectoRepository;
import com.smartpoweron.gestion_materiales.repository.PruebaCalidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;

@Service
public class CalidadService {

    @Autowired private PruebaCalidadRepository pruebaRepository;
    @Autowired private ParametroPruebaRepository parametroRepository;
    @Autowired private ProyectoRepository proyectoRepository;

    public List<ParametroPrueba> listarParametros() {
        return parametroRepository.findAll();
    }

    public ParametroPrueba crearParametro(String tipoPrueba, String unidad, BigDecimal min, BigDecimal max, String normaReferencia) {
        if (tipoPrueba == null || tipoPrueba.trim().isEmpty()) {
            throw new RuntimeException("El tipo de prueba es obligatorio.");
        }
        if (unidad == null || unidad.trim().isEmpty()) {
            throw new RuntimeException("La unidad es obligatoria.");
        }
        if (max.compareTo(min) < 0) {
            throw new RuntimeException("El valor máximo no puede ser menor al mínimo.");
        }
        ParametroPrueba param = new ParametroPrueba();
        param.setTipoPrueba(tipoPrueba.trim());
        param.setUnidad(unidad.trim());
        param.setValorMinimo(min);
        param.setValorMaximo(max);
        param.setNormaReferencia(normaReferencia);
        return parametroRepository.save(param);
    }

    public ParametroPrueba actualizarParametro(Long idParametro, String tipoPrueba, String unidad,
                                               BigDecimal min, BigDecimal max, String normaReferencia) {
        if (max.compareTo(min) < 0) {
            throw new RuntimeException("El valor máximo no puede ser menor al mínimo.");
        }
        ParametroPrueba param = parametroRepository.findById(idParametro)
                .orElseThrow(() -> new RuntimeException("Parámetro no encontrado."));
        param.setTipoPrueba(tipoPrueba);
        param.setUnidad(unidad);
        param.setValorMinimo(min);
        param.setValorMaximo(max);
        param.setNormaReferencia(normaReferencia);
        return parametroRepository.save(param);
    }

    public ParametroPrueba obtenerParametroPorId(Long idParametro) {
        return parametroRepository.findById(idParametro)
                .orElseThrow(() -> new RuntimeException("Parámetro no encontrado (id=" + idParametro + ")."));
    }

    @Transactional
    public void eliminarParametro(Long idParametro) {
        ParametroPrueba param = obtenerParametroPorId(idParametro);
        try {
            parametroRepository.delete(param);
            parametroRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException(
                "No se puede eliminar: el parámetro tiene inspecciones de calidad asociadas.");
        }
    }

    public List<PruebaCalidad> listarPruebasPorProyecto(Long idProyecto) {
        return pruebaRepository.findByProyectoIdProyectoOrderByFechaPruebaDesc(idProyecto);
    }

    public PruebaCalidad registrarPrueba(Long idProyecto, Long idParametro, BigDecimal valorMedido, String observacion, Usuario inspector) {
        Proyecto proyecto = proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado."));
        ParametroPrueba param = parametroRepository.findById(idParametro)
                .orElseThrow(() -> new RuntimeException("Parámetro de prueba no encontrado."));

        boolean conforme = valorMedido.compareTo(param.getValorMinimo()) >= 0
                && valorMedido.compareTo(param.getValorMaximo()) <= 0;

        if (!conforme && (observacion == null || observacion.trim().isEmpty())) {
            throw new RuntimeException("La observación es obligatoria cuando la prueba es NO CONFORME.");
        }

        PruebaCalidad prueba = new PruebaCalidad();
        prueba.setProyecto(proyecto);
        prueba.setParametro(param);
        prueba.setUsuarioInspector(inspector);
        prueba.setValorMedido(valorMedido);
        prueba.setResultado(conforme ? "CONFORME" : "NO_CONFORME");
        prueba.setObservacion(observacion);
        return pruebaRepository.save(prueba);
    }

    public PruebaCalidad editarPrueba(Long idPrueba, BigDecimal valorMedido, String observacion) {
        PruebaCalidad prueba = pruebaRepository.findById(idPrueba)
                .orElseThrow(() -> new RuntimeException("Inspección no encontrada."));
        ParametroPrueba param = prueba.getParametro();

        boolean conforme = valorMedido.compareTo(param.getValorMinimo()) >= 0
                && valorMedido.compareTo(param.getValorMaximo()) <= 0;

        if (!conforme && (observacion == null || observacion.trim().isEmpty())) {
            throw new RuntimeException("La observación es obligatoria cuando la prueba es NO CONFORME.");
        }

        prueba.setValorMedido(valorMedido);
        prueba.setResultado(conforme ? "CONFORME" : "NO_CONFORME");
        prueba.setObservacion(observacion);
        return pruebaRepository.save(prueba);
    }

    public PruebaCalidad obtenerPruebaPorId(Long idPrueba) {
        return pruebaRepository.findById(idPrueba)
                .orElseThrow(() -> new RuntimeException("Inspección no encontrada (id=" + idPrueba + ")."));
    }

    @Transactional
    public void eliminarPrueba(Long idPrueba) {
        PruebaCalidad p = obtenerPruebaPorId(idPrueba);
        try {
            pruebaRepository.delete(p);
            pruebaRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("No se puede eliminar la inspección: tiene registros dependientes.");
        }
    }

    private PdfPCell crearCeldaHeader(String texto, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setBackgroundColor(new Color(15, 23, 42));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(6);
        return cell;
    }

    private PdfPCell crearCeldaDato(String texto, Font font, int alineacion) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setHorizontalAlignment(alineacion);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5);
        return cell;
    }

    public byte[] generarCertificadoPdf(Long idProyecto) {
        Proyecto proyecto = proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado."));
        List<PruebaCalidad> pruebas = pruebaRepository.findByProyectoIdProyectoOrderByFechaPruebaDesc(idProyecto);

        if (pruebas.isEmpty()) {
            throw new RuntimeException("El proyecto no tiene pruebas registradas.");
        }

        boolean hayNoConformes = pruebas.stream().anyMatch(p -> "NO_CONFORME".equals(p.getResultado()));

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, 30, 30, 30, 30);
            PdfWriter.getInstance(doc, out);
            doc.open();

            Font fTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 15, new Color(15, 23, 42));
            Font fSubtitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11,
                    hayNoConformes ? new Color(220, 38, 38) : new Color(2, 132, 199));
            Font fSeccion = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.BLACK);
            Font fHeaderTabla = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, Color.WHITE);
            Font fTexto = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.BLACK);
            Font fBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.BLACK);
            Font fConforme = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, new Color(22, 163, 74));
            Font fNoConforme = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, new Color(220, 38, 38));

            doc.add(new Paragraph("SMART POWER ON S.A.C. — PLANTA COMAS, LIMA", fTitulo));
            doc.add(new Paragraph(hayNoConformes
                    ? "PROTOCOLO DE INSPECCIÓN FAT — OBSERVADO (PENDIENTE DE LEVANTAR)"
                    : "PROTOCOLO Y CERTIFICADO DE CONFORMIDAD DE PRUEBAS FAT", fSubtitulo));
            doc.add(new Paragraph(" "));

            PdfPTable infoTabla = new PdfPTable(2);
            infoTabla.setWidthPercentage(100);
            infoTabla.setWidths(new float[]{25f, 75f});
            infoTabla.addCell(new Phrase("Código Proyecto:", fBold));
            infoTabla.addCell(new Phrase(proyecto.getCodigo(), fTexto));
            infoTabla.addCell(new Phrase("Cliente / Obra:", fBold));
            infoTabla.addCell(new Phrase(proyecto.getCliente(), fTexto));
            doc.add(infoTabla);
            doc.add(new Paragraph(" "));
            String[] itemsMM = {
                "1. Soportes", "2. Esquineros", "3. Tapas laterales", "4. Tapas ciegas",
                "5. Pisos", "6. Canaletas instaladas", "7. Rieles instalados", "8. Torque de pernos",
                "9. Kit antisísmico", "10. Colocación de bornes", "11. Colocación de breaker"
            };

            String[] itemsEL = {
                "1. Timbrado", "2. Amarillado de circuito tensión CC", "3. Amarillado de circuito de mandos",
                "4. Amarillado de circuito de señales", "5. Amarillado de circuito de alterna"
            };

            for (PruebaCalidad p : pruebas) {
                String tipo = p.getParametro().getTipoPrueba();
                boolean esConforme = "CONFORME".equals(p.getResultado());
                String obs = p.getObservacion() != null ? p.getObservacion() : "";

                doc.add(new Paragraph("INSPECCIÓN " + tipo + " — " + p.getParametro().getNormaReferencia(), fSeccion));
                doc.add(new Paragraph(" "));

                PdfPTable tCheck = new PdfPTable(5);
                tCheck.setWidthPercentage(100);
                tCheck.setWidths(new float[]{32f, 18f, 16f, 20f, 14f});
                tCheck.addCell(crearCeldaHeader("Ítem Verificado", fHeaderTabla));
                tCheck.addCell(crearCeldaHeader("Verificación", fHeaderTabla));
                tCheck.addCell(crearCeldaHeader("Estado", fHeaderTabla));
                tCheck.addCell(crearCeldaHeader("Realizado por", fHeaderTabla));
                tCheck.addCell(crearCeldaHeader("Fecha", fHeaderTabla));

                String[] lista = "METALMECANICA".equals(tipo) ? itemsMM : itemsEL;
                for (String item : lista) {
                    String nombreLimpio = item.substring(item.indexOf('.') + 2);
                    boolean itemFallo = !esConforme && obs.contains(nombreLimpio);

                    tCheck.addCell(crearCeldaDato(item, fTexto, Element.ALIGN_LEFT));
                    tCheck.addCell(crearCeldaDato(itemFallo ? "no/observado" : "si/conforme",
                            itemFallo ? fNoConforme : fConforme, Element.ALIGN_CENTER));
                    tCheck.addCell(crearCeldaDato(" ", fTexto, Element.ALIGN_CENTER));
                    tCheck.addCell(crearCeldaDato(" ", fTexto, Element.ALIGN_CENTER));
                    tCheck.addCell(crearCeldaDato(" ", fTexto, Element.ALIGN_CENTER));
                }
                doc.add(tCheck);

                PdfPTable tObs = new PdfPTable(1);
                tObs.setWidthPercentage(100);
                PdfPCell cObs = new PdfPCell(new Phrase("Comentarios y Observaciones: "
                        + (obs.isEmpty() ? "Sin observaciones." : obs)
                        + "\nLevantamiento de Observación / Notas de Taller: __________________________________________________", fTexto));
                cObs.setBackgroundColor(new Color(248, 250, 252));
                cObs.setPadding(8);
                tObs.addCell(cObs);
                doc.add(tObs);
                doc.add(new Paragraph(" "));
            }

            doc.add(new Paragraph(" "));
            doc.add(new Paragraph("____________________________________                    ____________________________________", fTexto));
            doc.add(new Paragraph("      Firma Inspector Control de Calidad                                    V°B° Jefe de Producción - SPO", fBold));

            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF: " + e.getMessage());
        }
    }
}
