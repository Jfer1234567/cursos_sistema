import os
import sys
import docx
from docx import Document
from docx.shared import Inches, Pt, RGBColor, Cm
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT, WD_ALIGN_VERTICAL
from docx.oxml import parse_xml, OxmlElement
from docx.oxml.ns import nsdecls, qn

def build_informe_completo():
    doc = Document()

    # --- CONFIGURACIÓN DE PÁGINA (A4 / MÁRGENES INSTITUCIONALES) ---
    section = doc.sections[0]
    section.page_width = Cm(21.0)
    section.page_height = Cm(29.7)
    section.top_margin = Cm(2.5)
    section.bottom_margin = Cm(2.5)
    section.left_margin = Cm(3.0)
    section.right_margin = Cm(2.5)

    # --- PALETA DE COLORES INSTITUCIONAL UNA PUNO & IIICCD ---
    COLOR_GRANATE_HEX = "6D1A24"       # Granate UNA
    COLOR_ORO_HEX = "C99738"           # Oro Académico
    COLOR_AZUL_OSCURO_HEX = "1E293B"   # Azul Marino / Slate Dark
    COLOR_TEXTO_HEX = "334155"         # Texto Principal
    COLOR_TEXTO_MUTED_HEX = "64748B"   # Texto Secundario
    COLOR_FONDO_CLARO_HEX = "F8FAFC"   # Fondo de Tablas / Callouts
    COLOR_BORDE_HEX = "CBD5E1"         # Borde Suave
    COLOR_EXITO_HEX = "059669"         # Verde Verificación

    COLOR_GRANATE = RGBColor(109, 26, 36)
    COLOR_ORO = RGBColor(201, 151, 56)
    COLOR_AZUL_OSCURO = RGBColor(30, 41, 59)
    COLOR_TEXTO = RGBColor(51, 65, 85)
    COLOR_TEXTO_MUTED = RGBColor(100, 116, 139)

    # --- RUTAS DE RECURSOS ---
    base_dir = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
    logo_path = os.path.join(base_dir, "src", "main", "resources", "static", "images", "logo-iiiccd.jpeg")
    diagram_bd_path = os.path.join(base_dir, "informe", "diagrama_base_datos.png")
    output_docx_path = os.path.join(base_dir, "informe", "Informe_Tecnico_Sistema_Cursos_IIICCD.docx")

    # --- FUNCIONES DE FORMATEO Y ESTILOS ---
    def aplicar_sombreado(cell, color_hex):
        shd = parse_xml(f'<w:shd {nsdecls("w")} w:fill="{color_hex}"/>')
        cell._tc.get_or_add_tcPr().append(shd)

    def aplicar_margenes_celda(cell, top=130, bottom=130, left=150, right=150):
        tcMar = parse_xml(
            f'<w:tcMar {nsdecls("w")}>'
            f'<w:top w:w="{top}" w:type="dxa"/>'
            f'<w:bottom w:w="{bottom}" w:type="dxa"/>'
            f'<w:left w:w="{left}" w:type="dxa"/>'
            f'<w:right w:w="{right}" w:type="dxa"/>'
            f'</w:tcMar>'
        )
        cell._tc.get_or_add_tcPr().append(tcMar)

    def configurar_bordes_tabla(table, color_hex=COLOR_BORDE_HEX):
        tblPr = table._tbl.tblPr
        borders = parse_xml(
            f'<w:tblBorders {nsdecls("w")}>'
            f'<w:top w:val="single" w:sz="6" w:space="0" w:color="{color_hex}"/>'
            f'<w:bottom w:val="single" w:sz="6" w:space="0" w:color="{color_hex}"/>'
            f'<w:left w:val="none"/>'
            f'<w:right w:val="none"/>'
            f'<w:insideH w:val="single" w:sz="4" w:space="0" w:color="{color_hex}"/>'
            f'<w:insideV w:val="none"/>'
            f'</w:tblBorders>'
        )
        tblPr.append(borders)

    def add_h1(text):
        p = doc.add_paragraph()
        p.paragraph_format.space_before = Pt(22)
        p.paragraph_format.space_after = Pt(6)
        p.paragraph_format.keep_with_next = True
        run = p.add_run(text)
        run.font.name = "Arial"
        run.font.size = Pt(14)
        run.font.bold = True
        run.font.color.rgb = COLOR_GRANATE
        return p

    def add_h2(text):
        p = doc.add_paragraph()
        p.paragraph_format.space_before = Pt(14)
        p.paragraph_format.space_after = Pt(4)
        p.paragraph_format.keep_with_next = True
        run = p.add_run(text)
        run.font.name = "Arial"
        run.font.size = Pt(11.5)
        run.font.bold = True
        run.font.color.rgb = COLOR_AZUL_OSCURO
        return p

    def add_h3(text):
        p = doc.add_paragraph()
        p.paragraph_format.space_before = Pt(10)
        p.paragraph_format.space_after = Pt(2)
        p.paragraph_format.keep_with_next = True
        run = p.add_run(text)
        run.font.name = "Arial"
        run.font.size = Pt(10.5)
        run.font.bold = True
        run.font.color.rgb = COLOR_GRANATE
        return p

    def add_p(text, bold_prefix=None, space_after=5):
        p = doc.add_paragraph()
        p.alignment = WD_ALIGN_PARAGRAPH.JUSTIFY
        p.paragraph_format.line_spacing = 1.15
        p.paragraph_format.space_after = Pt(space_after)

        if bold_prefix:
            r_pre = p.add_run(bold_prefix)
            r_pre.font.name = "Arial"
            r_pre.font.size = Pt(9.5)
            r_pre.font.bold = True
            r_pre.font.color.rgb = COLOR_AZUL_OSCURO

        r_text = p.add_run(text)
        r_text.font.name = "Arial"
        r_text.font.size = Pt(9.5)
        r_text.font.color.rgb = COLOR_TEXTO
        return p

    def add_bullet(text, bold_prefix=None):
        p = doc.add_paragraph(style='List Bullet')
        p.alignment = WD_ALIGN_PARAGRAPH.JUSTIFY
        p.paragraph_format.line_spacing = 1.15
        p.paragraph_format.space_after = Pt(3)

        if bold_prefix:
            r_pre = p.add_run(bold_prefix)
            r_pre.font.name = "Arial"
            r_pre.font.size = Pt(9.5)
            r_pre.font.bold = True
            r_pre.font.color.rgb = COLOR_AZUL_OSCURO

        r_text = p.add_run(text)
        r_text.font.name = "Arial"
        r_text.font.size = Pt(9.5)
        r_text.font.color.rgb = COLOR_TEXTO
        return p

    def add_callout(text, title=None, border_color_hex=COLOR_GRANATE_HEX):
        table = doc.add_table(rows=1, cols=1)
        table.alignment = WD_TABLE_ALIGNMENT.CENTER
        table.autofit = False

        cell = table.cell(0, 0)
        cell.width = Inches(6.0)
        aplicar_sombreado(cell, COLOR_FONDO_CLARO_HEX)
        aplicar_margenes_celda(cell, top=140, bottom=140, left=180, right=140)

        tcBorders = parse_xml(
            f'<w:tcBorders {nsdecls("w")}>'
            f'<w:top w:val="none"/>'
            f'<w:left w:val="single" w:sz="24" w:space="0" w:color="{border_color_hex}"/>'
            f'<w:bottom w:val="none"/>'
            f'<w:right w:val="none"/>'
            f'</w:tcBorders>'
        )
        cell._tc.get_or_add_tcPr().append(tcBorders)

        p = cell.paragraphs[0]
        p.alignment = WD_ALIGN_PARAGRAPH.JUSTIFY
        p.paragraph_format.line_spacing = 1.15
        p.paragraph_format.space_after = Pt(0)

        if title:
            rt = p.add_run(title + "\n")
            rt.font.name = "Arial"
            rt.font.size = Pt(9.5)
            rt.font.bold = True
            rt.font.color.rgb = COLOR_GRANATE

        rtxt = p.add_run(text)
        rtxt.font.name = "Arial"
        rtxt.font.size = Pt(9.0)
        rtxt.font.color.rgb = COLOR_TEXTO

        p_sep = doc.add_paragraph()
        p_sep.paragraph_format.space_before = Pt(3)
        p_sep.paragraph_format.space_after = Pt(3)

    def add_table_data(headers, data, col_widths=None):
        table = doc.add_table(rows=len(data) + 1, cols=len(headers))
        table.alignment = WD_TABLE_ALIGNMENT.CENTER
        table.autofit = False
        configurar_bordes_tabla(table)

        hdr_cells = table.rows[0].cells
        for i, title in enumerate(headers):
            hdr_cells[i].text = title
            aplicar_sombreado(hdr_cells[i], COLOR_GRANATE_HEX)
            aplicar_margenes_celda(hdr_cells[i], top=140, bottom=140, left=120, right=120)
            p = hdr_cells[i].paragraphs[0]
            p.alignment = WD_ALIGN_PARAGRAPH.LEFT
            p.paragraph_format.space_after = Pt(0)
            for r in p.runs:
                r.font.name = "Arial"
                r.font.size = Pt(8.5)
                r.font.bold = True
                r.font.color.rgb = RGBColor(255, 255, 255)

        for row_idx, row_data in enumerate(data):
            row_cells = table.rows[row_idx + 1].cells
            bg_hex = COLOR_FONDO_CLARO_HEX if (row_idx % 2 == 1) else "FFFFFF"
            for col_idx, cell_value in enumerate(row_data):
                row_cells[col_idx].text = str(cell_value)
                aplicar_sombreado(row_cells[col_idx], bg_hex)
                aplicar_margenes_celda(row_cells[col_idx], top=100, bottom=100, left=120, right=120)
                p = row_cells[col_idx].paragraphs[0]
                p.alignment = WD_ALIGN_PARAGRAPH.LEFT
                p.paragraph_format.space_after = Pt(0)
                p.paragraph_format.line_spacing = 1.15
                for r in p.runs:
                    r.font.name = "Arial"
                    r.font.size = Pt(8.0)
                    r.font.color.rgb = COLOR_TEXTO

        if col_widths:
            for row in table.rows:
                for idx, width in enumerate(col_widths):
                    row.cells[idx].width = Inches(width)

        p_sep = doc.add_paragraph()
        p_sep.paragraph_format.space_before = Pt(3)
        p_sep.paragraph_format.space_after = Pt(3)

    # Configuración de Encabezado y Pie de página
    header = section.header
    p_head = header.paragraphs[0]
    p_head.alignment = WD_ALIGN_PARAGRAPH.RIGHT
    run_head = p_head.add_run("IIICCD — Universidad Nacional del Altiplano de Puno | Informe Técnico Oficial")
    run_head.font.name = "Arial"
    run_head.font.size = Pt(8)
    run_head.font.color.rgb = COLOR_TEXTO_MUTED

    footer = section.footer
    p_foot = footer.paragraphs[0]
    p_foot.alignment = WD_ALIGN_PARAGRAPH.LEFT
    run_foot = p_foot.add_run("Sistema Web de Gestión Académica y Certificación Digital con QR — Octubre 2026")
    run_foot.font.name = "Arial"
    run_foot.font.size = Pt(8)
    run_foot.font.color.rgb = COLOR_TEXTO_MUTED

    # =========================================================================
    # 1. PORTADA INSTITUCIONAL DE GALA
    # =========================================================================
    p_inst = doc.add_paragraph()
    p_inst.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p_inst.paragraph_format.space_before = Pt(10)
    p_inst.paragraph_format.space_after = Pt(2)
    r_uni = p_inst.add_run("UNIVERSIDAD NACIONAL DEL ALTIPLANO DE PUNO\n")
    r_uni.font.name = "Arial"
    r_uni.font.size = Pt(13)
    r_uni.font.bold = True
    r_uni.font.color.rgb = COLOR_GRANATE

    r_fac = p_inst.add_run("FACULTAD DE INGENIERÍA ESTADÍSTICA E INFORMÁTICA\n")
    r_fac.font.name = "Arial"
    r_fac.font.size = Pt(11)
    r_fac.font.bold = True
    r_fac.font.color.rgb = COLOR_AZUL_OSCURO

    r_inst = p_inst.add_run("INSTITUTO DE INVESTIGACIÓN EN INTELIGENCIA COMPUTACIONAL Y CIENCIA DE DATOS (IIICCD)")
    r_inst.font.name = "Arial"
    r_inst.font.size = Pt(10)
    r_inst.font.bold = True
    r_inst.font.color.rgb = COLOR_ORO

    # Logotipo Oficial Centrado
    if os.path.exists(logo_path):
        p_logo = doc.add_paragraph()
        p_logo.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p_logo.paragraph_format.space_before = Pt(14)
        p_logo.paragraph_format.space_after = Pt(14)
        p_logo.add_run().add_picture(logo_path, width=Inches(1.9))

    # Título Principal
    p_tit = doc.add_paragraph()
    p_tit.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p_tit.paragraph_format.space_before = Pt(8)
    p_tit.paragraph_format.space_after = Pt(6)
    r_tit = p_tit.add_run("INFORME TÉCNICO DE ARQUITECTURA, DESARROLLO E IMPLEMENTACIÓN DEL SISTEMA WEB INTEGRAL DE GESTIÓN ACADÉMICA, INSCRIPCIONES, PASARELA YAPE Y CERTIFICACIÓN DIGITAL CON VALIDACIÓN QR")
    r_tit.font.name = "Arial"
    r_tit.font.size = Pt(13)
    r_tit.font.bold = True
    r_tit.font.color.rgb = COLOR_GRANATE

    # Subtítulo
    p_sub = doc.add_paragraph()
    p_sub.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p_sub.paragraph_format.space_before = Pt(0)
    p_sub.paragraph_format.space_after = Pt(18)
    r_sub = p_sub.add_run("Especificación de Requerimientos (IEEE 830), Modelo Relacional de Base de Datos, Auditoría Financiera de Vouchers y Emisión Solemne de Diplomas con Snapshots Inmutables")
    r_sub.font.name = "Arial"
    r_sub.font.size = Pt(10)
    r_sub.font.color.rgb = COLOR_AZUL_OSCURO

    # Ficha Técnica de Portada
    t_meta = doc.add_table(rows=6, cols=2)
    t_meta.alignment = WD_TABLE_ALIGNMENT.CENTER
    t_meta.autofit = False
    configurar_bordes_tabla(t_meta)

    meta_items = [
        ("Institución Patrocinadora:", "Universidad Nacional del Altiplano de Puno (UNA Puno)"),
        ("Unidad Académica Responsable:", "Instituto de Investigación en Inteligencia Computacional (IIICCD - FINESI)"),
        ("Director del Instituto:", "Dr. Leonid Alemán Gonzales"),
        ("Versión del Software:", "Versión 1.0.0 (Release Cloud Producción)"),
        ("Repositorio Oficial GitHub:", "https://github.com/Jfer1234567/cursos_sistema"),
        ("Entorno Desplegado en la Nube:", "https://cursos-sistema.onrender.com (Web Service + Neon PostgreSQL)")
    ]

    for idx, (label, val) in enumerate(meta_items):
        row = t_meta.rows[idx]
        row.cells[0].width = Inches(2.2)
        row.cells[1].width = Inches(3.8)
        row.cells[0].text = label
        row.cells[1].text = val

        aplicar_sombreado(row.cells[0], COLOR_FONDO_CLARO_HEX)
        aplicar_sombreado(row.cells[1], "FFFFFF")
        aplicar_margenes_celda(row.cells[0], top=70, bottom=70, left=90, right=90)
        aplicar_margenes_celda(row.cells[1], top=70, bottom=70, left=90, right=90)

        p0 = row.cells[0].paragraphs[0]
        p0.alignment = WD_ALIGN_PARAGRAPH.RIGHT
        p0.paragraph_format.space_after = Pt(0)
        for r in p0.runs:
            r.font.name = "Arial"
            r.font.size = Pt(8.5)
            r.font.bold = True
            r.font.color.rgb = COLOR_AZUL_OSCURO

        p1 = row.cells[1].paragraphs[0]
        p1.alignment = WD_ALIGN_PARAGRAPH.LEFT
        p1.paragraph_format.space_after = Pt(0)
        for r in p1.runs:
            r.font.name = "Arial"
            r.font.size = Pt(8.5)
            r.font.color.rgb = COLOR_TEXTO

    p_fecha = doc.add_paragraph()
    p_fecha.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p_fecha.paragraph_format.space_before = Pt(20)
    r_f = p_fecha.add_run("Puno, Perú — Octubre de 2026")
    r_f.font.name = "Arial"
    r_f.font.size = Pt(9)
    r_f.font.bold = True
    r_f.font.color.rgb = COLOR_TEXTO_MUTED

    doc.add_page_break()

    # =========================================================================
    # ÍNDICE GENERAL Y ESPECIFICACIONES TÉCNICAS
    # =========================================================================
    add_h1("ÍNDICE GENERAL DEL DOCUMENTO")
    
    indice_data = [
        ["Capítulo 1", "Resumen Ejecutivo y Objetivos del Proyecto", "Pág. 3"],
        ["Capítulo 2", "Especificación Formal de Requerimientos de Software (IEEE 830)", "Pág. 4"],
        ["", "2.1 Requerimientos Funcionales del Sistema (RF-01 a RF-22)", ""],
        ["", "2.2 Requerimientos No Funcionales del Sistema (RNF-01 a RNF-10)", ""],
        ["", "2.3 Matriz de Trazabilidad Requerimientos vs Componentes", ""],
        ["Capítulo 3", "Arquitectura del Sistema y Stack Tecnológico de Vanguardia", "Pág. 7"],
        ["Capítulo 4", "Modelado de Datos, Gráfica ERD y Diccionario de Entidades", "Pág. 9"],
        ["", "4.1 Diagrama Gráfico Entidad-Relación (PostgreSQL 16)", ""],
        ["", "4.2 Análisis de Cardinalidades y Reglas de Integridad Referencial", ""],
        ["", "4.3 Diccionario Detallado de Tablas y Atributos", ""],
        ["Capítulo 5", "Descripción Funcional Detallada de Módulos Implementados", "Pág. 12"],
        ["", "5.1 Portal Público, Identidad Académica y Vitrina de Convocatorias", ""],
        ["", "5.2 Módulo de Registro y Validación Rigurosa de Identidad (DNI/Celular)", ""],
        ["", "5.3 Pasarela de Pago Yape y Subida Inteligente de Comprobantes", ""],
        ["", "5.4 Panel de Control del Participante (Mis Cursos, Meet, WhatsApp)", ""],
        ["", "5.5 Panel de Administración y Gestión Académica Integral", ""],
        ["", "5.6 Módulo Oficial de Certificación Digital y Validación Pública QR", ""],
        ["", "5.7 Generador y Estudio Oficial de Afiches Publicitarios (Flyer Studio)", ""],
        ["Capítulo 6", "Seguridad Informática, Rendimiento y Buenas Prácticas", "Pág. 18"],
        ["Capítulo 7", "Infraestructura Cloud, Despliegue Continuo y Mantenimiento", "Pág. 20"],
        ["Capítulo 8", "Aseguramiento de Calidad y Pruebas Automatizadas (100%)", "Pág. 22"],
        ["Capítulo 9", "Conclusiones, Impacto Institucional y Recomendaciones", "Pág. 23"],
        ["Sección Final", "Constancia Institucional de Conformidad y Firmas", "Pág. 24"]
    ]
    add_table_data(["Sección", "Contenido Temático", "Referencia"], indice_data, [1.2, 4.0, 0.8])

    doc.add_page_break()

    # =========================================================================
    # CAPÍTULO 1: RESUMEN EJECUTIVO Y OBJETIVOS
    # =========================================================================
    add_h1("CAPÍTULO 1: RESUMEN EJECUTIVO Y OBJETIVOS DEL PROYECTO")

    add_h2("1.1 Introducción")
    add_p(
        "El presente informe técnico expone el diseño, desarrollo, modelado de base de datos, pruebas y puesta en producción del Sistema Web Oficial "
        "de Gestión Académica, Inscripciones y Certificación Digital para el Instituto de Investigación en Inteligencia Computacional "
        "y Ciencia de Datos (IIICCD), órgano adscrito a la Facultad de Ingeniería Estadística e Informática (FINESI) de la Universidad "
        "Nacional del Altiplano de Puno (UNA Puno)."
    )

    add_h2("1.2 Justificación Institucional y Problemática Previa")
    add_p("Con anterioridad a la implementación del sistema, los procesos del instituto presentaban las siguientes debilidades críticas:")
    add_bullet("Dispersión de inscripciones en Google Forms y mensajes privados de WhatsApp, causando descontrol y pérdida de información.", "Matrícula Manual: ")
    add_bullet("Cotejo manual de vouchers de Yape en chats, con alto riesgo de reutilización fraudulenta o montos no coincidentes.", "Auditoría Financiera Ineficiente: ")
    add_bullet("Elaboración manual de certificados en software de diseño gráfico, con demoras de semanas y errores tipográficos en nombres y DNI.", "Emisión Vulnerable: ")
    add_bullet("Inexistencia de un portal público de consulta QR para que empleadores y universidades verifiquen la autenticidad de los diplomas.", "Cero Trazabilidad Pública: ")

    add_h2("1.3 Objetivos del Proyecto")
    add_p("Objetivo General:", bold_prefix=None)
    add_p(
        "Desarrollar e implementar una plataforma web institucional integral para el IIICCD - UNA Puno que automatice la gestión de cursos, "
        "el proceso de inscripción y pago mediante Yape, el control de accesos a clases virtuales y la emisión de certificados digitales auditables con validación QR."
    )
    add_p("Objetivos Específicos:")
    add_bullet("Levantar y documentar formalmente los requerimientos funcionales y no funcionales bajo el estándar internacional IEEE 830.")
    add_bullet("Diseñar y normalizar el modelo de base de datos relacional en PostgreSQL 16 con integridad referencial e índices optimizados.")
    add_bullet("Implementar la pasarela de pago Yape con carga segura de vouchers y compresión automática en el navegador (Canvas).")
    add_bullet("Construir el generador y estudio oficial de afiches (Flyer Studio) con 3 temas institucionales y descarga PNG a 2.5x.")
    add_bullet("Desarrollar el motor de diplomas PDF con OpenPDF (marcas de agua vectoriales, firmas oficiales y código QR de validación pública).")
    add_bullet("Desplegar la infraestructura en la nube con Docker multi-stage, Neon PostgreSQL Serverless y Cloudinary CDN.")

    doc.add_page_break()

    # =========================================================================
    # CAPÍTULO 2: ESPECIFICACIÓN FORMAL DE REQUERIMIENTOS (IEEE 830)
    # =========================================================================
    add_h1("CAPÍTULO 2: ESPECIFICACIÓN FORMAL DE REQUERIMIENTOS DE SOFTWARE (IEEE 830)")

    add_h2("2.1 Metodología de Requerimientos")
    add_p(
        "Para garantizar un desarrollo riguroso y alineado con los estándares de ingeniería de software, los requerimientos del sistema "
        "fueron especificados siguiendo la norma internacional IEEE 830 (Recommended Practice for Software Requirements Specifications). "
        "Se clasificaron en Requerimientos Funcionales (RF), que definen el comportamiento y servicios del sistema, y Requerimientos No Funcionales (RNF), "
        "que establecen los atributos de calidad, rendimiento, seguridad y restricciones de la plataforma."
    )

    add_h2("2.2 Catálogo de Requerimientos Funcionales (RF)")
    add_p("A continuación, se presenta la matriz completa de los 22 requerimientos funcionales implementados y verificados al 100%:")

    rf_data = [
        ["RF-01", "Portal Público", "Visualización de la página principal con presentación institucional, estadísticas, marcas de agua del isotipo neural y vitrina flotante del afiche oficial más reciente.", "Alta", "Público"],
        ["RF-02", "Catálogo Académico", "Exploración y filtrado interactivo de cursos y convocatorias activas clasificadas por Líneas de Investigación (Inteligencia Computacional, Ciencia de Datos, Tecnologías Emergentes).", "Alta", "Público"],
        ["RF-03", "Ficha del Curso", "Visualización del perfil del ponente (foto, grado y cargo), temario, carga horaria, créditos universitarios y tarifas diferenciadas (Comunidad UNA vs General).", "Alta", "Público"],
        ["RF-04", "Registro Estricto", "Registro de usuarios con validación obligatoria de DNI peruano (exactamente 8 dígitos numéricos) o documento extranjero (hasta 12 alfanuméricos) y teléfono móvil peruano (9 dígitos).", "Alta", "Postulante"],
        ["RF-05", "Autenticación y Sesión", "Inicio de sesión seguro mediante correo y contraseña encriptada con BCrypt, con redirección automática basada en roles (Participante vs Administrador).", "Alta", "Todos"],
        ["RF-06", "Solicitud de Matrícula", "Iniciación de matrícula en un curso publicado con validación de vacantes disponibles y bloqueo formal de postulaciones duplicadas.", "Alta", "Participante"],
        ["RF-07", "Pasarela Yape Dinámica", "Presentación de instrucciones de pago con código QR, número telefónico institucional y titular configurables dinámicamente desde el panel administrativo.", "Alta", "Participante"],
        ["RF-08", "Carga de Comprobante", "Subida de la captura del voucher de Yape con autocompresión en cliente (HTML5 Canvas) a ~250KB y registro obligatorio del número de operación bancario.", "Alta", "Participante"],
        ["RF-09", "Panel del Participante", "Dashboard personal que lista el estado de todas las inscripciones del estudiante (`Pendiente`, `En Verificación`, `Aprobada`, `Rechazada`, `Completada`).", "Alta", "Participante"],
        ["RF-10", "Acceso a Sala Virtual", "Habilitación automática y protegida de los botones `📹 Unirse a la Clase (Zoom/Meet)` y `💬 Grupo de WhatsApp` exclusivamente para alumnos con pago aprobado.", "Alta", "Participante"],
        ["RF-11", "Descarga de Diplomas", "Visualización web del diploma digital de honor y descarga directa del certificado oficial en formato PDF de alta resolución firmado por la directiva.", "Alta", "Participante"],
        ["RF-12", "Dashboard Ejecutivo", "Panel con indicadores analíticos en tiempo real: cursos activos, pagos pendientes, alumnos aprobados, diplomas emitidos, usuarios registrados y mensajes nuevos.", "Alta", "Administrador"],
        ["RF-13", "Gestión de Cursos (CRUD)", "Creación, edición y control de estado de cursos (`Borrador`, `Publicado`, `En Curso`, `Finalizado`), precios diferenciados, cupos, enlaces virtuales y afiches.", "Alta", "Administrador"],
        ["RF-14", "Auditoría de Vouchers", "Bandeja de verificación de pagos con inspección visual del voucher en alta definición, aprobación con descuento automático de cupos, o rechazo motivado.", "Alta", "Administrador"],
        ["RF-15", "Alertas Asíncronas Mail", "Despacho de notificaciones por correo electrónico al estudiante al registrarse el voucher, al ser aprobado (con enlace a clase) o al ser observado.", "Media", "Sistema"],
        ["RF-16", "Directorio de Usuarios", "Módulo paginado con buscador por nombre, DNI o correo, filtros por curso o sin cursos, y botón para enlace directo a conversación de WhatsApp institucional.", "Media", "Administrador"],
        ["RF-17", "Exportación a Excel", "Descarga con un clic del padrón oficial de inscritos por curso en archivo CSV/Excel con codificación UTF-8 BOM para soporte nativo de tildes en Microsoft Excel.", "Alta", "Administrador"],
        ["RF-18", "Flyer Studio Oficial", "Generador interactivo de afiches publicitarios con 3 temas institucionales (Granate, Cyber Dark, Esmeralda), recorte circular de foto y renderizado PNG 2.5x.", "Media", "Administrador"],
        ["RF-19", "Certificación Inmutable", "Emisión automatizada de certificados al completar el curso, creando un snapshot histórico inmutable de los datos del estudiante, curso, ponente y créditos.", "Alta", "Sistema"],
        ["RF-20", "Validación Pública QR", "Portal público `/verificar` y `/verificar/{codigo}` que permite a empleadores escanear el QR o ingresar el código alfanumérico para certificar autenticidad.", "Alta", "Público"],
        ["RF-21", "Centro de Mensajes", "Procesamiento de consultas ciudadanas desde `/contacto`, con registro en base de datos, notificación por correo y respuestas directas vía mail o WhatsApp.", "Media", "Público/Admin"],
        ["RF-22", "Configuración de Cobros", "Módulo administrativo para modificar el número de Yape, titular y cargar un nuevo código QR sin reiniciar la aplicación ni tocar código.", "Media", "Administrador"]
    ]
    add_table_data(["Código", "Módulo", "Descripción del Requerimiento Funcional", "Prioridad", "Actor"], rf_data, [0.8, 1.2, 3.0, 0.8, 0.8])

    add_h2("2.3 Catálogo de Requerimientos No Funcionales (RNF)")
    add_p("A continuación, se especifican los 10 requerimientos no funcionales y atributos de calidad del sistema:")

    rnf_data = [
        ["RNF-01", "Rendimiento", "Tiempo de respuesta en navegación inferior a 100 ms gracias a prefetching predictivo en frontend y caché en memoria RAM en Spring Boot.", "Latencia < 100 ms"],
        ["RNF-02", "Seguridad Criptográfica", "Almacenamiento de contraseñas mediante algoritmo hash BCrypt con factor de costo 10. Protección activa contra ataques CSRF y Clickjacking.", "BCrypt Cost 10 / CSRF On"],
        ["RNF-03", "Control de Acceso (RBAC)", "Separación estricta de privilegios basada en roles (`ROLE_PARTICIPANTE`, `ROLE_ADMIN`). Redirección y bloqueo inmediato a peticiones no autorizadas.", "Spring Security 6"],
        ["RNF-04", "Integridad Histórica", "Garantía de inmutabilidad de certificados emitidos mediante snapshots desacoplados de la entidad curso, impidiendo adulteraciones posteriores.", "Snapshot Inmutable"],
        ["RNF-05", "Disponibilidad Cloud", "Disponibilidad del servicio del 99.5% mediante contenedorización Docker en Render y base de datos distribuida serverless en Neon PostgreSQL.", "Uptime 99.5%"],
        ["RNF-06", "Resiliencia Multimedia", "Almacenamiento dual de afiches y comprobantes en Cloudinary CDN con fallback automático en disco local en caso de fallos de conexión externa.", "Dual Storage / Fallback"],
        ["RNF-07", "Diseño Responsivo", "Interfaz visual completamente adaptable a resoluciones móviles (smartphones), tablets y pantallas de escritorio (Desktop Full HD / 2K).", "WCAG 2.1 / Responsive"],
        ["RNF-08", "Estética Solemne", "Diseño sobrio y académico representativo de la UNA Puno, con erradicación total de emojis informales, empleando tipografía limpia e iconos SVG.", "Identidad UNA Puno"],
        ["RNF-09", "Compatibilidad de Datos", "Exportación de padrones en formato CSV delimitado por punto y coma con cabecera UTF-8 BOM, garantizando compatibilidad nativa con Microsoft Excel.", "RFC 4180 / UTF-8 BOM"],
        ["RNF-10", "Mantenibilidad", "Arquitectura en capas (Layered Architecture) con bajo acoplamiento y alta cohesión, documentada y respaldada por 30 pruebas automatizadas.", "JUnit 5 (100% Pass)"]
    ]
    add_table_data(["Código", "Categoría", "Descripción y Criterio de Aceptación", "Métrica / Estándar"], rnf_data, [0.8, 1.4, 2.8, 1.4])

    doc.add_page_break()

    # =========================================================================
    # CAPÍTULO 3: ARQUITECTURA Y STACK TECNOLÓGICO
    # =========================================================================
    add_h1("CAPÍTULO 3: ARQUITECTURA DEL SISTEMA Y STACK TECNOLÓGICO")

    add_h2("3.1 Arquitectura en Capas (Layered MVC)")
    add_p(
        "El sistema fue construido bajo una arquitectura modular en capas que separa de forma nítida la interfaz de usuario, "
        "los controladores de flujo, la lógica de negocio y el acceso a datos relacionales:"
    )
    add_bullet("Vistas Thymeleaf 3 con HTML5 semántico, CSS Modular (variables institucionales) y JavaScript Vanilla para máxima velocidad sin sobrecarga de frameworks.", "Capa de Presentación: ")
    add_bullet("Controladores Spring MVC organizados por contexto (`PublicController`, `AuthController`, `ParticipanteController`, `AdminCursoController`, `CertificadoController`).", "Capa de Controladores: ")
    add_bullet("Servicios Spring (`CursoService`, `InscripcionService`, `PagoService`, `CertificadoService`, `CorreoService`) con control transaccional declarativo (`@Transactional`).", "Capa de Servicios de Negocio: ")
    add_bullet("Repositorios Spring Data JPA basados en Hibernate 6 con consultas optimizadas mediante `JOIN FETCH` para eliminar el problema N+1.", "Capa de Acceso a Datos: ")
    add_bullet("Base de datos relacional PostgreSQL 16 sobre Neon Serverless y almacenamiento distribuido en Cloudinary CDN.", "Capa de Persistencia y Nube: ")

    add_h2("3.2 Stack Tecnológico Enterprise")
    tech_data = [
        ["Java 17 LTS", "Lenguaje Core Backend", "Tipado estático seguro, Garbage Collector de alto rendimiento y soporte de largo plazo."],
        ["Spring Boot 4.1.1", "Framework Enterprise", "Configuración automática, inyección de dependencias robusta y métricas de producción."],
        ["Spring Security 6", "Seguridad y RBAC", "Protección contra ataques CSRF, fijación de sesión y encriptación de claves con BCrypt."],
        ["Spring Data JPA / Hibernate", "Mapeo Objeto-Relacional", "Abstracción eficiente de base de datos, consultas declarativas y control transaccional ACID."],
        ["Thymeleaf 3", "Motor de Plantillas", "Integración con Spring Security, dialecto estricto y caché en producción."],
        ["OpenPDF 2.0.3", "Motor de Diplomas PDF", "Renderizado vectorial milimétrico, marcas de agua al 8.5% (`PdfGState`), orlas y firmas."],
        ["PostgreSQL 16", "Motor de Base de Datos", "Cumplimiento ACID estricto, alta concurrencia y despliegue serverless en Neon."],
        ["Cloudinary SDK 1.39", "Almacenamiento Cloud", "Gestión de activos digitales (afiches y vouchers) en red CDN con entrega segura HTTPS."],
        ["Docker & Alpine Linux", "Contenedorización", "Empaquetado inmutable multi-stage con Eclipse Temurin JRE 17 Alpine (< 200 MB)."]
    ]
    add_table_data(["Tecnología", "Área de Aplicación", "Justificación Técnica"], tech_data, [1.5, 1.5, 3.0])

    doc.add_page_break()

    # =========================================================================
    # CAPÍTULO 4: MODELADO DE DATOS Y GRÁFICA DE BASE DE DATOS
    # =========================================================================
    add_h1("CAPÍTULO 4: MODELADO DE DATOS, GRÁFICA ERD Y DICCIONARIO DE ENTIDADES")

    add_h2("4.1 Diagrama Gráfico Entidad-Relación (PostgreSQL 16)")
    add_p(
        "El esquema relacional fue diseñado siguiendo los principios de la Tercera Forma Normal (3FN), garantizando la integridad "
        "referencial, unicidad de identificadores, ausencia de dependencias transitivas y soporte para auditoría transaccional. "
        "A continuación, se presenta la gráfica oficial del modelo relacional implementado en PostgreSQL 16:"
    )

    # Inserción de la imagen del diagrama de base de datos
    if os.path.exists(diagram_bd_path):
        p_img = doc.add_paragraph()
        p_img.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p_img.paragraph_format.space_before = Pt(8)
        p_img.paragraph_format.space_after = Pt(2)
        run_img = p_img.add_run()
        run_img.add_picture(diagram_bd_path, width=Inches(6.2))

        p_cap = doc.add_paragraph()
        p_cap.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p_cap.paragraph_format.space_before = Pt(2)
        p_cap.paragraph_format.space_after = Pt(12)
        r_cap = p_cap.add_run("Figura 4.1: Diagrama Entidad-Relación y Estructura Físico-Relacional (PostgreSQL 16) — Sistema Web IIICCD.")
        r_cap.font.name = "Arial"
        r_cap.font.size = Pt(8.5)
        r_cap.font.italic = True
        r_cap.font.color.rgb = COLOR_TEXTO_MUTED

    add_h2("4.2 Análisis de Cardinalidades y Reglas de Integridad Referencial")
    add_p("El diagrama relacional establece las siguientes reglas y asociaciones estructurales:")
    add_bullet(
        "Una línea de investigación agrupa una o más áreas científicas especializadas. La eliminación de una línea está restringida para preservar la categorización histórica.",
        "Relación Líneas de Investigación a Áreas (1 : N): "
    )
    add_bullet(
        "Un área de investigación contiene múltiples cursos y diplomados. Cada curso pertenece obligatoriamente a un área temática.",
        "Relación Áreas de Investigación a Cursos (1 : N): "
    )
    add_bullet(
        "Un usuario participante puede matricularse en múltiples cursos distintos a lo largo del tiempo. Se aplica una restricción de unicidad lógica para impedir que un usuario tenga más de una inscripción activa para el mismo curso.",
        "Relación Usuarios a Inscripciones (1 : N): "
    )
    add_bullet(
        "Un curso publicado admite múltiples inscripciones de participantes hasta alcanzar el límite máximo estipulado en `cupos_totales`.",
        "Relación Cursos a Inscripciones (1 : N): "
    )
    add_bullet(
        "Cada inscripción formalizada con pago genera exactamente un registro financiero auditado en la tabla `pagos`, con clave foránea `inscripcion_id`.",
        "Relación Inscripciones a Pagos (1 : 1): "
    )
    add_bullet(
        "Cada inscripción culminada exitosamente (`COMPLETADA`) genera exactamente un certificado oficial de acreditación con snapshot inmutable.",
        "Relación Inscripciones a Certificados (1 : 1): "
    )
    add_bullet(
        "Cada comprobante aprobado registra la clave foránea del administrador que auditó el voucher (`verificado_por_id`), garantizando trazabilidad y no repudio.",
        "Relación de Auditoría Administrador a Pagos (1 : N): "
    )

    add_h2("4.3 Diccionario Detallado de Tablas de la Base de Datos")

    add_h3("A. Tabla: `usuarios` (Identidad y Seguridad)")
    usr_data = [
        ["id", "BIGSERIAL (PK)", "Identificador unívoco del usuario."],
        ["correo", "VARCHAR(150) UNIQUE", "Correo electrónico utilizado como credencial de acceso."],
        ["password", "VARCHAR(255)", "Hash criptográfico de la contraseña (BCrypt costo 10)."],
        ["nombre_completo", "VARCHAR(150)", "Nombres y apellidos completos formalmente registrados."],
        ["tipo_documento", "VARCHAR(20)", "Tipo de documento: `DNI`, `PASAPORTE`, `CARNET_EXTRANJERIA`."],
        ["numero_documento", "VARCHAR(20) UNIQUE", "DNI (exactamente 8 dígitos peruanos) o documento extranjero."],
        ["telefono", "VARCHAR(20)", "Teléfono celular peruano (9 dígitos con prefijo 9 para WhatsApp)."],
        ["institucion", "VARCHAR(150)", "Universidad o entidad académica/laboral de procedencia."],
        ["rol", "VARCHAR(20)", "Rol de autorización: `PARTICIPANTE` o `ADMIN`."],
        ["activo", "BOOLEAN", "Estado operativo de la cuenta de usuario."]
    ]
    add_table_data(["Campo", "Tipo de Dato", "Descripción / Regla de Negocio"], usr_data, [1.5, 1.8, 2.7])

    add_h3("B. Tabla: `cursos` (Oferta Académica)")
    curso_data = [
        ["id", "BIGSERIAL (PK)", "Identificador unívoco del curso."],
        ["area_investigacion_id", "BIGINT (FK)", "Área de investigación a la que pertenece el curso."],
        ["nombre", "VARCHAR(200)", "Título formal del curso de especialización o taller."],
        ["descripcion_corta", "VARCHAR(255)", "Resumen ejecutivo para tarjetas de catálogo."],
        ["docente_responsable", "VARCHAR(150)", "Nombre completo del ponente o docente titular."],
        ["docente_cargo", "VARCHAR(150)", "Grado académico y especialidad del ponente."],
        ["docente_foto_url", "VARCHAR(255)", "Ruta segura de la fotografía del docente."],
        ["precio", "NUMERIC(10,2)", "Tarifa de inscripción para público en general."],
        ["precio_comunidad", "NUMERIC(10,2)", "Tarifa preferencial para la comunidad universitaria UNA Puno."],
        ["cupos_totales", "INTEGER", "Capacidad máxima de participantes permitidos."],
        ["cupos_disponibles", "INTEGER", "Vacantes disponibles en tiempo real."],
        ["estado", "VARCHAR(20)", "Estado del curso: `BORRADOR`, `PUBLICADO`, `EN_CURSO`, `FINALIZADO`."],
        ["enlace_clase", "VARCHAR(255)", "Enlace virtual a la sala de Zoom o Google Meet (protegido)."],
        ["enlace_whatsapp", "VARCHAR(255)", "Enlace de invitación al grupo privado de WhatsApp."],
        ["creditos", "INTEGER", "Valor curricular en créditos universitarios oficiales."],
        ["flyer_url", "VARCHAR(255)", "URL del afiche oficial alojado en Cloudinary/local."],
        ["flyer_publicado", "BOOLEAN", "Indicador booleano para activar el afiche en la portada."]
    ]
    add_table_data(["Campo", "Tipo de Dato", "Descripción / Regla de Negocio"], curso_data, [1.6, 1.6, 2.8])

    add_h3("C. Tabla: `inscripciones` (Matrículas Estudiantiles)")
    ins_data = [
        ["id", "BIGSERIAL (PK)", "Identificador unívoco de la inscripción."],
        ["usuario_id", "BIGINT (FK)", "Participante matriculado (asociado a `usuarios.id`)."],
        ["curso_id", "BIGINT (FK)", "Curso seleccionado (asociado a `cursos.id`)."],
        ["estado", "VARCHAR(30)", "Estado: `PENDIENTE_PAGO`, `PENDIENTE_VERIFICACION`, `APROBADA`, `RECHAZADA`, `COMPLETADA`."],
        ["fecha_inscripcion", "TIMESTAMP", "Fecha y hora exacta de creación de la matrícula."],
        ["notas_admin", "VARCHAR(255)", "Observaciones registradas en caso de comprobante rechazado."]
    ]
    add_table_data(["Campo", "Tipo de Dato", "Descripción / Regla de Negocio"], ins_data, [1.5, 1.8, 2.7])

    add_h3("D. Tabla: `pagos` (Transacciones Financieras Yape)")
    pago_data = [
        ["id", "BIGSERIAL (PK)", "Identificador unívoco del pago auditado."],
        ["inscripcion_id", "BIGINT (FK)", "Inscripción vinculada a la transacción."],
        ["monto", "NUMERIC(10,2)", "Importe económico transferido por el participante."],
        ["metodo_pago", "VARCHAR(20)", "Método de pago: `YAPE`."],
        ["referencia_externa", "VARCHAR(50)", "Número de operación o código de aprobación bancario."],
        ["comprobante_url", "VARCHAR(255)", "URL segura del voucher en Cloudinary o disco local."],
        ["estado", "VARCHAR(20)", "Estado financiero: `PENDIENTE`, `APROBADO`, `RECHAZADO`."],
        ["fecha_pago", "TIMESTAMP", "Fecha y hora en que el alumno adjuntó el comprobante."],
        ["fecha_verificacion", "TIMESTAMP", "Fecha y hora en que la administración auditó el pago."],
        ["verificado_por_id", "BIGINT (FK)", "Administrador responsable de la auditoría."]
    ]
    add_table_data(["Campo", "Tipo de Dato", "Descripción / Regla de Negocio"], pago_data, [1.6, 1.6, 2.8])

    add_h3("E. Tabla: `certificados` (Acreditaciones Oficiales Inmutables)")
    cert_data = [
        ["id", "BIGSERIAL (PK)", "Identificador unívoco del certificado."],
        ["codigo_unico", "VARCHAR(50) UNIQUE", "Código alfanumérico seguro para consulta pública (UUID)."],
        ["inscripcion_id", "BIGINT (FK)", "Inscripción aprobada que originó el diploma."],
        ["nombre_estudiante", "VARCHAR(150)", "Snapshot inmutable del nombre del estudiante al egresar."],
        ["documento_estudiante", "VARCHAR(20)", "Snapshot inmutable del DNI del participante."],
        ["nombre_curso", "VARCHAR(200)", "Snapshot inmutable del nombre del curso completado."],
        ["docente", "VARCHAR(150)", "Snapshot inmutable del ponente del curso."],
        ["duracion", "VARCHAR(50)", "Snapshot inmutable de la carga horaria académica."],
        ["creditos", "INTEGER", "Snapshot inmutable de los créditos universitarios asignados."],
        ["fecha_emision", "TIMESTAMP", "Fecha y hora formal de expedición del documento."],
        ["url_verificacion", "VARCHAR(255)", "Enlace web directo de validación pública."]
    ]
    add_table_data(["Campo", "Tipo de Dato", "Descripción / Regla de Negocio"], cert_data, [1.7, 1.6, 2.7])

    add_h3("F. Tablas Complementarias: `configuracion_pago` y `mensajes_contacto`")
    add_bullet("Almacena de forma dinámica el número telefónico de Yape, el nombre del titular y el código QR oficial, modificables desde el panel administrativo sin tocar código fuente.", "Configuración de Pago (`configuracion_pago`): ")
    add_bullet("Bandeja de consultas ciudadanas enviadas desde el formulario público de contacto, con control de lectura, fecha y respuestas directas por correo o WhatsApp.", "Mensajes de Contacto (`mensajes_contacto`): ")

    doc.add_page_break()

    # =========================================================================
    # CAPÍTULO 5: DESCRIPCIÓN FUNCIONAL DETALLADA DE MÓDULOS
    # =========================================================================
    add_h1("CAPÍTULO 5: DESCRIPCIÓN FUNCIONAL DETALLADA DE MÓDULOS IMPLEMENTADOS")

    add_h2("5.1 Portal Público e Identidad Académica Institucional")
    add_bullet("Encabezado de cristal translúcido fijado (`position: sticky`) con desenfoque de fondo que mantiene accesibles los enlaces de navegación, catálogo y autenticación.", "Navbar Glassmorphism: ")
    add_bullet("Presentación con malla neuronal vectorial SVG, resplandores multicapa en granate institucional (#6D1A24) y oro (#C99738), tarjeta de estadísticas reflectiva y marca de agua institucional del Isotipo puro.", "Hero Section Tecnológico: ")
    add_bullet("Módulo flotante destacado en portada (`.flyer-vitrina-card`) con bordes dorados y sombra tridimensional donde se exhibe el afiche oficial del curso más reciente, excluido automáticamente de la cuadrícula inferior para evitar duplicidad visual.", "Vitrina Flotante de Convocatorias: ")
    add_bullet("Cuadrícula interactiva con filtrado en tiempo real por Líneas de Investigación, mostrando precio general y preferencial, cupos vacantes y badge de estado.", "Catálogo Académico: ")
    add_bullet("Ficha exhaustiva con temario, perfil del ponente (foto, cargo y especialidad), créditos académicos y botón inteligente de inscripción.", "Ficha Detallada del Curso: ")

    add_h2("5.2 Módulo de Registro y Validación Rigurosa de Identidad")
    add_bullet("Al seleccionar DNI, el campo bloquea letras y caracteres especiales, restringiendo la longitud a exactamente 8 dígitos numéricos mediante expresiones regulares (`^[0-9]{8}$`). Si se elige Pasaporte o Carné de Extranjería, admite hasta 12 caracteres alfanuméricos.", "Validación de Documento de Identidad: ")
    add_bullet("Se restringe a exactamente 9 dígitos numéricos iniciando con 9 (`^9[0-9]{8}$`), garantizando que sea un número celular peruano apto para el enlace automático a WhatsApp.", "Validación de Celular Peruano: ")
    add_bullet("El sistema valida la inexistencia de correos o números de documento repetidos, mostrando alertas visuales inline sin perder los datos previamente digitados.", "Prevención de Duplicados: ")

    add_h2("5.3 Pasarela de Pago Yape y Subida Inteligente de Comprobantes")
    add_bullet("El estudiante visualiza el código QR oficial de Yape, el número telefónico institucional y el titular configurados por la administración.", "Instrucciones Claras y QR Dinámico: ")
    add_bullet("El estudiante adjunta la captura del voucher y digita el número de operación que figura en su aplicativo bancario.", "Formulario de Declaración: ")
    add_bullet("Antes de iniciar la subida de red, un script en JavaScript intercepta la imagen fotográfica (que en teléfonos suele pesar entre 3MB y 8MB) y la redimensiona/comprime dinámicamente mediante HTML5 Canvas a ~250KB con calidad 0.85, reduciendo el tiempo de subida en más del 90%.", "Autocompresión de Imágenes en Cliente: ")
    add_bullet("El archivo se almacena en Cloudinary bajo la carpeta `cursos_sistema/comprobantes` con enlace seguro HTTPS, contando con fallback en disco local.", "Almacenamiento Seguro: ")
    add_bullet("Al registrarse el comprobante, el participante recibe un correo electrónico confirmando la recepción y notificándole que su vacante ha sido reservada.", "Notificación Inmediata: ")

    add_h2("5.4 Panel de Control del Participante (Estudiante)")
    add_bullet("Listado de todos los cursos a los que ha postulado, con badges visuales de estado (`Pendiente`, `En Verificación`, `Aprobada`, `Completada`).", "Mis Cursos y Estado de Matrícula: ")
    add_bullet("Cuando la administración aprueba el pago, el panel activa de inmediato el botón destacado `📹 Unirse a la Clase en Vivo (Zoom / Meet)` que redirige a la sala virtual oficial.", "Acceso Seguro a Clases Virtuales: ")
    add_bullet("Botón directo que abre el aplicativo WhatsApp con el enlace de invitación al grupo privado de estudiantes del curso.", "Grupo Oficial de WhatsApp: ")
    add_bullet("Al culminar el curso, el participante puede visualizar su diploma en la web o descargarlo en PDF de alta resolución con un solo clic.", "Descarga Directa de Certificado: ")

    add_h2("5.5 Panel de Administración y Gestión Académica Integral")
    add_bullet("Tarjetas analíticas con indicadores en tiempo real: Cursos Activos, Pagos Pendientes, Alumnos Aprobados, Diplomas Emitidos, Usuarios Registrados y Mensajes Nuevos.", "Dashboard Ejecutivo: ")
    add_bullet("Formularios para crear y editar cursos, fijar precios diferenciados, asignar cupos, ingresar enlaces de Meet y WhatsApp, cargar foto del ponente y publicar el afiche en portada.", "Gestión Integral de Cursos: ")
    add_bullet("Lista interactiva donde el administrador examina la captura del voucher en alta resolución, coteja el número de operación y aprueba la matrícula (descontando automáticamente una vacante) o la rechaza ingresando el motivo con notificación por correo.", "Bandeja de Auditoría de Pagos: ")
    add_bullet("Directorio paginado con buscador por nombre, DNI o correo, con pestañas interactivas para filtrar participantes por curso específico o sin cursos, y botón directo a WhatsApp.", "Directorio de Usuarios: ")
    add_bullet("Descarga instantánea del listado oficial de inscritos por curso en formato CSV/Excel con codificación UTF-8 BOM para apertura nativa en Microsoft Excel.", "Exportación de Padrón a Excel: ")
    add_bullet("Bandeja con buscador, badges de estado (`NUEVO`, `Leído`, `Atendido`) y botones de respuesta rápida vía `mailto:` o WhatsApp.", "Centro de Consultas de Contacto: ")
    add_bullet("Módulo para actualizar el número de Yape, titular y cargar un nuevo código QR sin reiniciar el servidor.", "Configuración Dinámica de Cobros: ")

    add_h2("5.6 Generador y Estudio Oficial de Afiches (Flyer Studio)")
    add_bullet("Granate UNA (institucional), Cyber Dark (moderno tecnológico) y Deep Emerald (académico científico).", "Tres Temas Visuales Oficiales: ")
    add_bullet("Formato Vertical (4:5 para WhatsApp e Instagram) y Formato Cuadrado (1:1 para publicaciones y Facebook).", "Dos Formatos Estándar: ")
    add_bullet("Permite seleccionar cualquier curso del sistema y poblar instantáneamente título, ponente, fecha, horas, precios y certificaciones.", "Sincronización Automática: ")
    add_bullet("Carga interactiva con previsualización circular instantánea y recorte institucional.", "Foto Profesional del Docente: ")
    add_bullet("Mediante `html2canvas`, el afiche se rasteriza a escala 2.5x produciendo una imagen PNG nítida lista para imprimir o compartir, permitiendo además copiar la imagen directamente al portapapeles.", "Exportación en Alta Resolución (2.5x): ")

    add_h2("5.7 Módulo Oficial de Certificación Digital y Validación QR")
    add_bullet("Al completarse un curso, se genera un código alfanumérico único (UUID) y se guarda una copia inmutable de los datos del estudiante, curso, ponente, horas y créditos en la tabla `certificados`.", "Snapshots Inmutables de Seguridad: ")
    add_bullet("Vista en pantalla completa con medallón digital dorado animado en relieve, marca de agua del Isotipo puro de red neuronal, orla ornamental perimetral en Granate y Oro, y botón Add to LinkedIn.", "Diploma Digital Web Interactivo: ")
    add_bullet("Motor OpenPDF configurado para trazar la orla universitaria, aplicar el Isotipo en capa inferior translúcida al 8.5% (`PdfGState`), incrustar firmas de la Directiva y Decanatura, y generar el código QR vectorial.", "Generador de PDF de Gala Universitario: ")
    add_bullet("Ruta abierta (`/verificar` y `/verificar/{codigo}`) donde cualquier entidad o empleador puede escanear el QR o ingresar el código alfanumérico para comprobar la legitimidad del diploma y descargar el documento PDF original.", "Portal Público de Validación Universitaria: ")

    doc.add_page_break()

    # =========================================================================
    # CAPÍTULO 6: SEGURIDAD, RENDIMIENTO Y BUENAS PRÁCTICAS
    # =========================================================================
    add_h1("CAPÍTULO 6: SEGURIDAD INFORMÁTICA, RENDIMIENTO Y BUENAS PRÁCTICAS")

    add_h2("6.1 Mecanismos de Seguridad y Control de Acceso")
    add_bullet("Configuración estricta de `SecurityFilterChain` dividiendo las rutas en públicas, de participantes autenticados y administrativas, bloqueando accesos no autorizados.", "Control Basado en Roles (RBAC): ")
    add_bullet("Todas las contraseñas se cifran mediante la función hash criptográfica de una vía BCrypt con factor de costo 10 antes de persistirse en base de datos.", "Criptografía de Contraseñas: ")
    add_bullet("Activación estricta de tokens CSRF en todos los formularios POST, protegiendo al sistema contra peticiones maliciosas forjadas desde sitios externos.", "Protección CSRF: ")
    add_bullet("Los comprobantes de pago se sirven a través del endpoint protegido `/comprobantes/{archivo}`, el cual valida la sesión y restringe la visualización exclusivamente al propietario y administradores.", "Privacidad Financiera: ")

    add_h2("6.2 Optimización de Rendimiento y Latencia de Red")
    add_bullet("Sustitución de consultas perezosas en bucles por sentencias `JOIN FETCH` en `CursoRepository`, `PagoRepository` e `InscripcionRepository`, consolidando decenas de peticiones de red en 1 sola consulta SQL.", "Eliminación de Consultas N+1: ")
    add_bullet("Configuración del pool HikariCP con `minimum-idle=5` y `maximum-pool-size=10`, manteniendo conexiones TLS calientes preestablecidas hacia Neon DB (Ohio) sin retardos de negociación SSL.", "Conexiones Calientes (HikariCP): ")
    add_bullet("Habilitación de compresión GZIP en el servidor para documentos HTML, CSS, JavaScript y JSON, reduciendo el tamaño de transferencia en más de un 70%.", "Compresión HTTP GZIP: ")
    add_bullet("Configuración de `Cache-Control: max-age=604800, public` (7 días) para hojas de estilo, scripts y logotipos, permitiendo que carguen en 0 ms desde la caché del navegador.", "Caché de Recursos Estáticos: ")
    add_bullet("Script en `main.js` que escucha eventos `mouseover` y `touchstart` sobre enlaces internos para precargar la página en segundo plano mediante `<link rel=\"prefetch\">`.", "Prefetching Predictivo al Hover: ")
    add_bullet("El servicio de envío de correos (`CorreoService`) opera de forma desacoplada con hilos en segundo plano (`@Async` y `TaskExecutor`), liberando la respuesta HTTP en menos de 50 ms.", "Desacoplamiento Asíncrono de Correos: ")
    add_bullet("Erradicación total de emojis informales en todas las vistas de la aplicación, adoptando una estética solemne, académica y rigurosa con iniciales monogramáticas y tipografía limpia.", "Estética Universitaria Rigurosa: ")

    doc.add_page_break()

    # =========================================================================
    # CAPÍTULO 7: INFRAESTRUCTURA CLOUD Y GUÍA DE DESPLIEGUE
    # =========================================================================
    add_h1("CAPÍTULO 7: INFRAESTRUCTURA CLOUD Y GUÍA DE DESPLIEGUE")

    add_h2("7.1 Arquitectura de Despliegue en la Nube")
    infra_data = [
        ["Servicio Web (Web Service)", "Render Cloud Platform (Plan Web Service). Aloja la aplicación Spring Boot compilada en un contenedor Docker optimizado, con auto-reinicio, HTTPS gratuito y sincronización con GitHub."],
        ["Base de Datos Serverless", "Neon PostgreSQL Cloud (AWS us-east-2). Base de datos relacional serverless con cifrado TLS forzado (`sslmode=require`), alta disponibilidad y copias de seguridad automáticas."],
        ["Almacenamiento Multimedia (CDN)", "Cloudinary Media Cloud. Gestión de afiches promocionales y vouchers de pago con URLs seguras HTTPS y entrega mediante CDN global."],
        ["Contenedor Docker Multi-Stage", "Construcción en 2 etapas: Etapa 1 compila el proyecto con Gradle y OpenJDK 17; Etapa 2 copia exclusivamente el archivo `.jar` resultante sobre una imagen ultraligera Alpine Linux con Eclipse Temurin JRE 17."]
    ]
    add_table_data(["Componente de Infraestructura", "Detalle de Configuración y Operación"], infra_data, [2.2, 3.8])

    add_h2("7.2 Matriz de Variables de Entorno de Producción")
    env_data = [
        ["PORT", "Puerto HTTP dinámico asignado por el proveedor de nube (por defecto: 8085)."],
        ["SPRING_DATASOURCE_URL", "Cadena de conexión JDBC PostgreSQL (ej. `jdbc:postgresql://ep-bold-...neon.tech/neondb?sslmode=require`)."],
        ["SPRING_DATASOURCE_USERNAME", "Usuario autorizado de la base de datos Neon PostgreSQL."],
        ["SPRING_DATASOURCE_PASSWORD", "Contraseña segura de acceso a la base de datos."],
        ["CLOUDINARY_CLOUD_NAME", "Identificador de la cuenta Cloudinary para almacenamiento."],
        ["CLOUDINARY_API_KEY", "Llave pública de API para autenticación en Cloudinary."],
        ["CLOUDINARY_API_SECRET", "Llave secreta de API para subida y gestión de archivos."],
        ["MAIL_USERNAME", "Cuenta oficial de correo electrónico institucional (Gmail SMTP)."],
        ["MAIL_PASSWORD", "Contraseña de aplicación de 16 caracteres para envío seguro SMTP."]
    ]
    add_table_data(["Variable de Entorno", "Propósito y Descripción"], env_data, [2.4, 3.6])

    doc.add_page_break()

    # =========================================================================
    # CAPÍTULO 8: PRUEBAS Y ASEGURAMIENTO DE CALIDAD
    # =========================================================================
    add_h1("CAPÍTULO 8: ASEGURAMIENTO DE CALIDAD Y PRUEBAS AUTOMATIZADAS (100%)")

    add_h2("8.1 Metodología de Pruebas")
    add_p(
        "Se diseñó una suite automatizada de pruebas unitarias y de integración ejecutadas mediante JUnit 5 y Spring Boot Test, "
        "utilizando una base de datos H2 en memoria para validar modelos, servicios y controladores de forma aislada y reproducible."
    )

    test_data = [
        ["CursosApplicationTests", "Inicialización del Contexto", "Verifica el levantamiento correcto de todos los beans de Spring Boot y componentes del sistema.", "1 / 1 Aprobada"],
        ["UsuarioServiceTest", "Servicio de Usuarios", "Valida el registro de estudiantes, hashing de contraseñas BCrypt, validación de DNI y prevención de correos duplicados.", "5 / 5 Aprobadas"],
        ["InscripcionServiceTest", "Servicio de Inscripciones", "Verifica el inicio de inscripción, bloqueo de inscripciones duplicadas, control de cupos y completado académico.", "6 / 6 Aprobadas"],
        ["PagoServiceTest", "Pasarela Financiera", "Prueba el registro de pagos Yape, aprobación de vouchers con descuento de cupos y rechazo con notas de observación.", "5 / 5 Aprobadas"],
        ["CursoServiceTest", "Gestión Académica", "Evalúa el listado de destacados, filtrado por líneas de investigación, gestión de cupos y activación de flyers.", "5 / 5 Aprobadas"],
        ["CertificadoServiceTest", "Certificación Digital", "Valida la emisión de certificados con snapshots inmutables, unicidad de código alfanumérico y consulta pública.", "4 / 4 Aprobadas"],
        ["CertificadoPdfGeneratorTest", "Motor de Diplomas PDF", "Verifica la generación del archivo binario PDF formal con OpenPDF, validando márgenes, orlas y QR.", "2 / 2 Aprobadas"],
        ["ArchivoServiceTest", "Procesamiento de Archivos", "Evalúa la subida y almacenamiento de recursos multimedia con fallback a almacenamiento local.", "2 / 2 Aprobadas"]
    ]
    add_table_data(["Clase de Prueba", "Módulo Auditado", "Objetivo de Validación", "Resultado"], test_data, [1.5, 1.3, 2.4, 0.8])

    add_callout(
        "MÉTRICA FINAL DE CALIDAD: 30 de 30 pruebas unitarias y de integración ejecutadas con éxito absoluto (100% de aprobación). "
        "Tiempo de ejecución: 50 segundos. Cero errores de compilación, cero fallos y cero pruebas ignoradas.",
        "Resultado del Control de Calidad Oficial",
        border_color_hex=COLOR_EXITO_HEX
    )

    doc.add_page_break()

    # =========================================================================
    # CAPÍTULO 9: CONCLUSIONES Y RECOMENDACIONES
    # =========================================================================
    add_h1("CAPÍTULO 9: CONCLUSIONES, IMPACTO INSTITUCIONAL Y RECOMENDACIONES")

    add_h2("9.1 Logros Principales del Proyecto")
    add_bullet("Se entregó una solución tecnológica completa, funcional y desplegada en producción que unifica la gestión de cursos, pagos, participantes y diplomas del IIICCD.", "Modernización Integral: ")
    add_bullet("El flujo de inscripción con Yape y la auditoría con autocompresión de vouchers agilizó el procesamiento administrativo de días a minutos.", "Eficiencia Operativa: ")
    add_bullet("La sustitución de certificados manuales por diplomas digitales generados con OpenPDF y validación pública QR erradica el riesgo de falsificaciones.", "Inviolabilidad Académica: ")
    add_bullet("La integración del generador de flyers permite al instituto diseñar y publicar piezas promocionales en segundos sin costes de licenciamiento externo.", "Autonomía en Marketing: ")

    add_h2("9.2 Impacto Institucional en la UNA Puno y la FINESI")
    add_p(
        "La implementación de este sistema posiciona al Instituto de Investigación en Inteligencia Computacional y Ciencia de Datos "
        "a la vanguardia tecnológica dentro de la Universidad Nacional del Altiplano, sentando un precedente de modernización digital "
        "para otras facultades e institutos de investigación de la región."
    )

    add_h2("9.3 Recomendaciones para Fases Futuras")
    add_bullet("Evaluar la integración de pasarelas de pago con tarjeta de crédito/débito (Niubiz o Culqi) en adición a Yape para estudiantes internacionales.", "Pasarelas Adicionales: ")
    add_bullet("Conectar el sistema al servidor de correo SMTP oficial institucional de la UNA Puno (@unap.edu.pe) para mayor solemnidad en notificaciones.", "Servidor de Correo Propio: ")
    add_bullet("Incorporar un módulo de encuestas de satisfacción estudiantil al culminar cada programa académico para retroalimentar la labor docente.", "Encuestas de Calidad: ")

    # =========================================================================
    # HOJA DE FIRMAS Y CONSTANCIA INSTITUCIONAL
    # =========================================================================
    add_h2("Constancia Institucional de Conformidad")
    add_p(
        "El presente informe técnico certifica la culminación satisfactoria de las etapas de análisis de requerimientos (IEEE 830), "
        "modelado relacional de base de datos, desarrollo, pruebas, control de calidad y despliegue en la nube del Sistema Web del IIICCD."
    )

    p_sig_space = doc.add_paragraph()
    p_sig_space.paragraph_format.space_before = Pt(45)

    t_firmas = doc.add_table(rows=1, cols=2)
    t_firmas.alignment = WD_TABLE_ALIGNMENT.CENTER
    t_firmas.autofit = False

    c0 = t_firmas.cell(0, 0)
    c1 = t_firmas.cell(0, 1)
    c0.width = Inches(3.0)
    c1.width = Inches(3.0)

    p_f0 = c0.paragraphs[0]
    p_f0.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p_f0.add_run("_________________________________________\n").font.color.rgb = COLOR_AZUL_OSCURO
    r_f0_name = p_f0.add_run("DR. LEONID ALEMÁN GONZALES\n")
    r_f0_name.font.name = "Arial"
    r_f0_name.font.bold = True
    r_f0_name.font.size = Pt(9.5)
    r_f0_name.font.color.rgb = COLOR_GRANATE
    r_f0_cargo = p_f0.add_run("Director del IIICCD\nFacultad de Ingeniería Estadística e Informática\nUniversidad Nacional del Altiplano de Puno")
    r_f0_cargo.font.name = "Arial"
    r_f0_cargo.font.size = Pt(8.5)
    r_f0_cargo.font.color.rgb = COLOR_TEXTO_MUTED

    p_f1 = c1.paragraphs[0]
    p_f1.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p_f1.add_run("_________________________________________\n").font.color.rgb = COLOR_AZUL_OSCURO
    r_f1_name = p_f1.add_run("EQUIPO DE DESARROLLO E INNOVACIÓN\n")
    r_f1_name.font.name = "Arial"
    r_f1_name.font.bold = True
    r_f1_name.font.size = Pt(9.5)
    r_f1_name.font.color.rgb = COLOR_AZUL_OSCURO
    r_f1_cargo = p_f1.add_run("Área de Ingeniería de Software e Inteligencia Computacional\nSistema Web de Cursos y Certificación Oficial\nUNA Puno - 2026")
    r_f1_cargo.font.name = "Arial"
    r_f1_cargo.font.size = Pt(8.5)
    r_f1_cargo.font.color.rgb = COLOR_TEXTO_MUTED

    # Guardar documento de forma resiliente
    try:
        doc.save(output_docx_path)
        print(f"Informe técnico enriquecido generado exitosamente en: {output_docx_path}")
        print(f"Tamaño del archivo Word generado: {os.path.getsize(output_docx_path)} bytes")
    except PermissionError:
        alt_path = os.path.join(base_dir, "informe", "Informe_Tecnico_Sistema_Cursos_IIICCD_Actualizado.docx")
        doc.save(alt_path)
        print(f"AVISO: El archivo principal está abierto en Microsoft Word.")
        print(f"Se ha guardado la versión actualizada con Requerimientos y Gráfica ERD en: {alt_path}")
        print(f"Tamaño del archivo Word generado: {os.path.getsize(alt_path)} bytes")

if __name__ == "__main__":
    build_informe_completo()
