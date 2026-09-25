package com.academy.cursos.util;

import com.academy.cursos.model.Certificado;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URL;
import java.time.format.DateTimeFormatter;

public class CertificadoPdfGenerator {

    private static final Color COLOR_PRIMARY = new Color(109, 26, 36);   // #6D1A24 - Granate UNA Puno
    private static final Color COLOR_SECONDARY = new Color(51, 65, 85);  // #334155 - Slate Oscuro
    private static final Color COLOR_GOLD = new Color(201, 151, 56);     // #C99738 - Oro Académico

    public static byte[] generarCertificadoPdf(Certificado cert) throws DocumentException {
        // Landscape A4 (842 x 595 pt)
        Document document = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter writer = PdfWriter.getInstance(document, out);
        document.open();

        float width = PageSize.A4.rotate().getWidth();
        float height = PageSize.A4.rotate().getHeight();

        // 1. Marca de Agua Central (Isotipo puro sin base ploma) en la capa inferior
        try {
            Image watermark = null;
            URL imgUrl = CertificadoPdfGenerator.class.getResource("/static/images/logo-isotipo-watermark.png");
            if (imgUrl != null) {
                watermark = Image.getInstance(imgUrl);
            } else {
                File localImg = new File("src/main/resources/static/images/logo-isotipo-watermark.png");
                if (localImg.exists()) {
                    watermark = Image.getInstance(localImg.getAbsolutePath());
                }
            }

            if (watermark != null) {
                watermark.scaleToFit(360, 360);
                float x = (width - watermark.getScaledWidth()) / 2f;
                float y = (height - watermark.getScaledHeight()) / 2f - 15f;
                watermark.setAbsolutePosition(x, y);

                PdfContentByte canvasUnder = writer.getDirectContentUnder();
                PdfGState gstate = new PdfGState();
                gstate.setFillOpacity(0.085f);
                canvasUnder.setGState(gstate);
                canvasUnder.addImage(watermark);
            }
        } catch (Exception ignored) {
            // Continuar si no se pudo cargar la imagen en pruebas locales
        }

        // 2. Marco Ornamental de Honor Universitario
        PdfContentByte canvas = writer.getDirectContent();

        // Marco exterior Granate UNA
        canvas.setColorStroke(COLOR_PRIMARY);
        canvas.setLineWidth(3.5f);
        canvas.rectangle(20, 20, width - 40, height - 40);
        canvas.stroke();

        // Marco interior Oro Académico
        canvas.setColorStroke(COLOR_GOLD);
        canvas.setLineWidth(1.5f);
        canvas.rectangle(26, 26, width - 52, height - 52);
        canvas.stroke();

        // Esquinas ornamentales con filete dorado
        float cornerSize = 14f;
        canvas.setLineWidth(1f);
        canvas.moveTo(26, height - 26 - cornerSize); canvas.lineTo(26 + cornerSize, height - 26); canvas.stroke();
        canvas.moveTo(width - 26, height - 26 - cornerSize); canvas.lineTo(width - 26 - cornerSize, height - 26); canvas.stroke();
        canvas.moveTo(26, 26 + cornerSize); canvas.lineTo(26 + cornerSize, 26); canvas.stroke();
        canvas.moveTo(width - 26, 26 + cornerSize); canvas.lineTo(width - 26 - cornerSize, 26); canvas.stroke();

        // 3. Tipografías Solemnes
        Font fontUniv = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, COLOR_SECONDARY);
        Font fontInst = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, COLOR_PRIMARY);
        Font fontOtorga = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, COLOR_GOLD);
        Font fontCertLabel = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, COLOR_PRIMARY);
        Font fontSubtitle = FontFactory.getFont(FontFactory.HELVETICA, 10, COLOR_SECONDARY);
        Font fontName = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, new Color(15, 23, 42));
        Font fontBody = FontFactory.getFont(FontFactory.HELVETICA, 11, COLOR_SECONDARY);
        Font fontCourse = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, COLOR_PRIMARY);
        Font fontDetails = FontFactory.getFont(FontFactory.HELVETICA, 9.5f, COLOR_SECONDARY);
        Font fontSignatures = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8.5f, new Color(15, 23, 42));
        Font fontSignTitle = FontFactory.getFont(FontFactory.HELVETICA, 7.5f, COLOR_SECONDARY);
        Font fontCode = FontFactory.getFont(FontFactory.COURIER_BOLD, 9, COLOR_PRIMARY);

        // 4. Cabecera Institucional
        try {
            URL logoUrl = CertificadoPdfGenerator.class.getResource("/static/images/logo-isotipo-watermark.png");
            Image logoHeader = null;
            if (logoUrl != null) {
                logoHeader = Image.getInstance(logoUrl);
            } else {
                File localLogo = new File("src/main/resources/static/images/logo-isotipo-watermark.png");
                if (localLogo.exists()) {
                    logoHeader = Image.getInstance(localLogo.getAbsolutePath());
                }
            }
            if (logoHeader != null) {
                logoHeader.scaleToFit(44, 44);
                logoHeader.setAlignment(Element.ALIGN_CENTER);
                logoHeader.setSpacingAfter(2);
                document.add(logoHeader);
            }
        } catch (Exception ignored) {}

        Paragraph pUniv = new Paragraph("UNIVERSIDAD NACIONAL DEL ALTIPLANO - PUNO\nFACULTAD DE INGENIERÍA ESTADÍSTICA E INFORMÁTICA", fontUniv);
        pUniv.setAlignment(Element.ALIGN_CENTER);
        pUniv.setSpacingAfter(2);
        document.add(pUniv);

        Paragraph pInst = new Paragraph("INSTITUTO DE INVESTIGACIÓN EN INTELIGENCIA COMPUTACIONAL Y CIENCIA DE DATOS", fontInst);
        pInst.setAlignment(Element.ALIGN_CENTER);
        pInst.setSpacingAfter(8);
        document.add(pInst);

        Paragraph pOtorga = new Paragraph("OTORGA EL PRESENTE", fontOtorga);
        pOtorga.setAlignment(Element.ALIGN_CENTER);
        pOtorga.setSpacingAfter(2);
        document.add(pOtorga);

        Paragraph pCert = new Paragraph("CERTIFICADO DE PARTICIPACIÓN", fontCertLabel);
        pCert.setAlignment(Element.ALIGN_CENTER);
        pCert.setSpacingAfter(6);
        document.add(pCert);

        Paragraph pA = new Paragraph("A favor de:", fontSubtitle);
        pA.setAlignment(Element.ALIGN_CENTER);
        pA.setSpacingAfter(2);
        document.add(pA);

        Paragraph pParticipante = new Paragraph(cert.getNombreParticipante().toUpperCase(), fontName);
        pParticipante.setAlignment(Element.ALIGN_CENTER);
        pParticipante.setSpacingAfter(6);
        document.add(pParticipante);

        Paragraph pTexto = new Paragraph("Por haber culminado y aprobado satisfactoriamente todas las exigencias académicas del curso de alta especialización en:", fontBody);
        pTexto.setAlignment(Element.ALIGN_CENTER);
        pTexto.setSpacingAfter(4);
        document.add(pTexto);

        Paragraph pCurso = new Paragraph("\"" + cert.getNombreCurso() + "\"", fontCourse);
        pCurso.setAlignment(Element.ALIGN_CENTER);
        pCurso.setSpacingAfter(6);
        document.add(pCurso);

        String textoDuracionYCreditos = cert.getDuracion();
        if (cert.getCreditos() != null && cert.getCreditos() > 0) {
            textoDuracionYCreditos += " (" + cert.getCreditos() + " créditos universitarios)";
        }

        Paragraph pArea = new Paragraph("Línea: " + cert.getLineaInvestigacion() + "  |  Área: " + cert.getAreaInvestigacion() +
                "\nDocente: " + cert.getDocente() + "  |  Duración: " + textoDuracionYCreditos, fontDetails);
        pArea.setAlignment(Element.ALIGN_CENTER);
        pArea.setSpacingAfter(10);
        document.add(pArea);

        // 5. Bloque Oficial de Firmas de Autoridades
        PdfPTable tableFirmas = new PdfPTable(2);
        tableFirmas.setWidthPercentage(78);
        tableFirmas.setSpacingBefore(12);

        // Firma 1: Director IIICCD
        PdfPCell cell1 = new PdfPCell();
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
        Paragraph pFirma1 = new Paragraph(
                "__________________________________________\n" +
                "DR. LEONID ALEMÁN GONZALES\n", fontSignatures
        );
        Paragraph pCargo1 = new Paragraph(
                "Director del Instituto de Investigación IIICCD\nFacultad de Ingeniería Estadística e Informática", fontSignTitle
        );
        pFirma1.setAlignment(Element.ALIGN_CENTER);
        pCargo1.setAlignment(Element.ALIGN_CENTER);
        cell1.addElement(pFirma1);
        cell1.addElement(pCargo1);
        tableFirmas.addCell(cell1);

        // Firma 2: Decanatura FINESI
        PdfPCell cell2 = new PdfPCell();
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        Paragraph pFirma2 = new Paragraph(
                "__________________________________________\n" +
                "DECANATURA / SECRETARÍA ACADÉMICA\n", fontSignatures
        );
        Paragraph pCargo2 = new Paragraph(
                "Facultad de Ingeniería Estadística e Informática\nUniversidad Nacional del Altiplano - Puno", fontSignTitle
        );
        pFirma2.setAlignment(Element.ALIGN_CENTER);
        pCargo2.setAlignment(Element.ALIGN_CENTER);
        cell2.addElement(pFirma2);
        cell2.addElement(pCargo2);
        tableFirmas.addCell(cell2);

        document.add(tableFirmas);

        // 6. Pie con Fecha y Código de Verificación
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy");
        String fechaStr = cert.getFechaEmision().format(dtf);

        Paragraph pFooter = new Paragraph(
                "Puno, " + fechaStr + "   •   Código Único de Validación: " + cert.getCodigoVerificacion() +
                "\nValide la autenticidad oficial de este documento en: http://localhost:8085/verificar?codigo=" + cert.getCodigoVerificacion(),
                fontCode
        );
        pFooter.setAlignment(Element.ALIGN_CENTER);
        pFooter.setSpacingBefore(12);
        document.add(pFooter);

        document.close();
        return out.toByteArray();
    }
}

