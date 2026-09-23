package com.academy.cursos.util;

import com.academy.cursos.model.Certificado;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

public class CertificadoPdfGenerator {

    private static final Color COLOR_PRIMARY = new Color(139, 26, 26);   // #8B1A1A
    private static final Color COLOR_SECONDARY = new Color(74, 74, 74);  // #4A4A4A
    private static final Color COLOR_GOLD = new Color(200, 169, 81);     // #C8A951

    public static byte[] generarCertificadoPdf(Certificado cert) throws DocumentException {
        // Landscape A4
        Document document = new Document(PageSize.A4.rotate(), 40, 40, 40, 40);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter writer = PdfWriter.getInstance(document, out);
        document.open();

        // Draw ornamental border
        PdfContentByte canvas = writer.getDirectContent();
        float width = PageSize.A4.rotate().getWidth();
        float height = PageSize.A4.rotate().getHeight();

        canvas.setColorStroke(COLOR_PRIMARY);
        canvas.setLineWidth(3);
        canvas.rectangle(20, 20, width - 40, height - 40);
        canvas.stroke();

        canvas.setColorStroke(COLOR_GOLD);
        canvas.setLineWidth(1);
        canvas.rectangle(25, 25, width - 50, height - 50);
        canvas.stroke();

        // Fonts
        Font fontUniv = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, COLOR_SECONDARY);
        Font fontInst = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, COLOR_PRIMARY);
        Font fontSubtitle = FontFactory.getFont(FontFactory.HELVETICA, 11, COLOR_SECONDARY);
        Font fontCertLabel = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, COLOR_GOLD);
        Font fontName = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, COLOR_PRIMARY);
        Font fontBody = FontFactory.getFont(FontFactory.HELVETICA, 12, COLOR_SECONDARY);
        Font fontCourse = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, COLOR_PRIMARY);
        Font fontDetails = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 11, COLOR_SECONDARY);
        Font fontCode = FontFactory.getFont(FontFactory.COURIER_BOLD, 10, COLOR_SECONDARY);

        // Header
        Paragraph pUniv = new Paragraph("UNIVERSIDAD NACIONAL DEL ALTIPLANO - PUNO\nFACULTAD DE INGENIERÍA ESTADÍSTICA E INFORMÁTICA", fontUniv);
        pUniv.setAlignment(Element.ALIGN_CENTER);
        pUniv.setSpacingAfter(4);
        document.add(pUniv);

        Paragraph pInst = new Paragraph("INSTITUTO DE INVESTIGACIÓN EN INTELIGENCIA COMPUTACIONAL\nY CIENCIA DE DATOS (IIICCD)", fontInst);
        pInst.setAlignment(Element.ALIGN_CENTER);
        pInst.setSpacingAfter(15);
        document.add(pInst);

        Paragraph pOtorga = new Paragraph("OTORGA EL PRESENTE", fontCertLabel);
        pOtorga.setAlignment(Element.ALIGN_CENTER);
        pOtorga.setSpacingAfter(2);
        document.add(pOtorga);

        Paragraph pCert = new Paragraph("CERTIFICADO DE PARTICIPACIÓN", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 26, COLOR_PRIMARY));
        pCert.setAlignment(Element.ALIGN_CENTER);
        pCert.setSpacingAfter(15);
        document.add(pCert);

        Paragraph pA = new Paragraph("A:", fontSubtitle);
        pA.setAlignment(Element.ALIGN_CENTER);
        pA.setSpacingAfter(5);
        document.add(pA);

        Paragraph pParticipante = new Paragraph(cert.getNombreParticipante().toUpperCase(), fontName);
        pParticipante.setAlignment(Element.ALIGN_CENTER);
        pParticipante.setSpacingAfter(15);
        document.add(pParticipante);

        Paragraph pTexto = new Paragraph("Por haber culminado y aprobado satisfactoriamente el curso especializado de capacitación profesional en:", fontBody);
        pTexto.setAlignment(Element.ALIGN_CENTER);
        pTexto.setSpacingAfter(8);
        document.add(pTexto);

        Paragraph pCurso = new Paragraph("\"" + cert.getNombreCurso() + "\"", fontCourse);
        pCurso.setAlignment(Element.ALIGN_CENTER);
        pCurso.setSpacingAfter(8);
        document.add(pCurso);

        Paragraph pArea = new Paragraph("Línea: " + cert.getLineaInvestigacion() + " | Área: " + cert.getAreaInvestigacion() +
                "\nDocente: " + cert.getDocente() + " | Duración: " + cert.getDuracion(), fontDetails);
        pArea.setAlignment(Element.ALIGN_CENTER);
        pArea.setSpacingAfter(25);
        document.add(pArea);

        // Date and verification
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy");
        String fechaStr = cert.getFechaEmision().format(dtf);

        Paragraph pFecha = new Paragraph("Puno, " + fechaStr, fontSubtitle);
        pFecha.setAlignment(Element.ALIGN_CENTER);
        pFecha.setSpacingAfter(20);
        document.add(pFecha);

        Paragraph pVerif = new Paragraph("Código Único de Validación: " + cert.getCodigoVerificacion() +
                "\nVerifique este documento en: /verificar?codigo=" + cert.getCodigoVerificacion(), fontCode);
        pVerif.setAlignment(Element.ALIGN_CENTER);
        document.add(pVerif);

        document.close();
        return out.toByteArray();
    }
}
