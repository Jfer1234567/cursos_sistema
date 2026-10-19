import os
import sys
import docx
from docx import Document
from docx.shared import Inches, Pt, RGBColor, Cm
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT, WD_ALIGN_VERTICAL
from docx.oxml import parse_xml, OxmlElement
from docx.oxml.ns import nsdecls, qn

def build_manual_usuario():
    doc = Document()

    # --- CONFIGURACIÓN DE PÁGINA (A4 / MÁRGENES INSTITUCIONALES) ---
    section = doc.sections[0]
    section.page_width = Cm(21.0)
    section.page_height = Cm(29.7)
    section.top_margin = Cm(2.2)
    section.bottom_margin = Cm(2.2)
    section.left_margin = Cm(2.8)
    section.right_margin = Cm(2.2)

    # --- PALETA INSTITUCIONAL UNA PUNO & IIICCD ---
    COLOR_GRANATE_HEX = "6D1A24"
    COLOR_ORO_HEX = "C99738"
    COLOR_AZUL_OSCURO_HEX = "1E293B"
    COLOR_TEXTO_HEX = "334155"
    COLOR_TEXTO_MUTED_HEX = "64748B"
    COLOR_FONDO_CLARO_HEX = "F8FAFC"
    COLOR_BORDE_HEX = "CBD5E1"
    COLOR_EXITO_HEX = "059669"

    COLOR_GRANATE = RGBColor(109, 26, 36)
    COLOR_ORO = RGBColor(201, 151, 56)
    COLOR_AZUL_OSCURO = RGBColor(30, 41, 59)
    COLOR_TEXTO = RGBColor(51, 65, 85)
    COLOR_TEXTO_MUTED = RGBColor(100, 116, 139)
    COLOR_EXITO = RGBColor(5, 150, 105)

    base_dir = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
    capturas_dir = os.path.join(base_dir, "informe", "capturas")
    logo_path = os.path.join(base_dir, "src", "main", "resources", "static", "images", "logo-iiiccd.jpeg")
    output_docx_path = os.path.join(base_dir, "informe", "Manual_de_Usuario_Sistema_Cursos_IIICCD.docx")

    # --- FUNCIONES DE ESTILO XML ---
    def aplicar_sombreado(cell, color_hex):
        shd = parse_xml(f'<w:shd {nsdecls("w")} w:fill="{color_hex}"/>')
        cell._tc.get_or_add_tcPr().append(shd)

    def aplicar_margenes_celda(cell, top=120, bottom=120, left=140, right=140):
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
        p.paragraph_format.space_before = Pt(20)
        p.paragraph_format.space_after = Pt(6)
        p.paragraph_format.keep_with_next = True
        run = p.add_run(text)
        run.font.name = "Arial"
        run.font.size = Pt(13.5)
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
        run.font.size = Pt(11)
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
        run.font.size = Pt(10)
        run.font.bold = True
        run.font.color.rgb = COLOR_GRANATE
        return p

    def add_p(text, bold_prefix=None, space_after=4):
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

    def add_step(num, title, text):
        p = doc.add_paragraph()
        p.alignment = WD_ALIGN_PARAGRAPH.JUSTIFY
        p.paragraph_format.line_spacing = 1.15
        p.paragraph_format.space_before = Pt(4)
        p.paragraph_format.space_after = Pt(4)

        r_num = p.add_run(f"Paso {num}: ")
        r_num.font.name = "Arial"
        r_num.font.size = Pt(9.5)
        r_num.font.bold = True
        r_num.font.color.rgb = COLOR_GRANATE

        r_tit = p.add_run(title + " — ")
        r_tit.font.name = "Arial"
        r_tit.font.size = Pt(9.5)
        r_tit.font.bold = True
        r_tit.font.color.rgb = COLOR_AZUL_OSCURO

        r_desc = p.add_run(text)
        r_desc.font.name = "Arial"
        r_desc.font.size = Pt(9.5)
        r_desc.font.color.rgb = COLOR_TEXTO
        return p

    def add_callout(text, title=None, border_color_hex=COLOR_GRANATE_HEX):
        table = doc.add_table(rows=1, cols=1)
        table.alignment = WD_TABLE_ALIGNMENT.CENTER
        table.autofit = False

        cell = table.cell(0, 0)
        cell.width = Inches(6.2)
        aplicar_sombreado(cell, COLOR_FONDO_CLARO_HEX)
        aplicar_margenes_celda(cell, top=120, bottom=120, left=160, right=120)

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
        p_sep.paragraph_format.space_before = Pt(2)
        p_sep.paragraph_format.space_after = Pt(2)

    def add_figure(filename, title_text, width_inches=6.0):
        img_full_path = os.path.join(capturas_dir, filename)
        if os.path.exists(img_full_path):
            p_img = doc.add_paragraph()
            p_img.alignment = WD_ALIGN_PARAGRAPH.CENTER
            p_img.paragraph_format.space_before = Pt(6)
            p_img.paragraph_format.space_after = Pt(2)
            p_img.paragraph_format.keep_with_next = True
            run_img = p_img.add_run()
            run_img.add_picture(img_full_path, width=Inches(width_inches))

            # Epígrafe formal
            p_cap = doc.add_paragraph()
            p_cap.alignment = WD_ALIGN_PARAGRAPH.CENTER
            p_cap.paragraph_format.space_before = Pt(2)
            p_cap.paragraph_format.space_after = Pt(8)
            p_cap.paragraph_format.keep_with_next = False
            r_cap = p_cap.add_run(title_text)
            r_cap.font.name = "Arial"
            r_cap.font.size = Pt(8.5)
            r_cap.font.italic = True
            r_cap.font.color.rgb = COLOR_TEXTO_MUTED
        else:
            add_callout(f"Captura no encontrada: {filename}", "AVISO DE IMAGEN", COLOR_ORO_HEX)

    def add_table_data(headers, data, col_widths=None):
        table = doc.add_table(rows=len(data) + 1, cols=len(headers))
        table.alignment = WD_TABLE_ALIGNMENT.CENTER
        table.autofit = False
        configurar_bordes_tabla(table)

        hdr_cells = table.rows[0].cells
        for i, title in enumerate(headers):
            hdr_cells[i].text = title
            aplicar_sombreado(hdr_cells[i], COLOR_GRANATE_HEX)
            aplicar_margenes_celda(hdr_cells[i], top=120, bottom=120, left=100, right=100)
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
            shd_col = COLOR_FONDO_CLARO_HEX if (row_idx % 2 == 1) else "FFFFFF"
            for col_idx, cell_value in enumerate(row_data):
                row_cells[col_idx].text = str(cell_value)
                aplicar_sombreado(row_cells[col_idx], shd_col)
                aplicar_margenes_celda(row_cells[col_idx], top=80, bottom=80, left=100, right=100)
                p = row_cells[col_idx].paragraphs[0]
                p.alignment = WD_ALIGN_PARAGRAPH.LEFT
                p.paragraph_format.space_after = Pt(0)
                for r in p.runs:
                    r.font.name = "Arial"
                    r.font.size = Pt(8.5)
                    r.font.color.rgb = COLOR_TEXTO

        if col_widths:
            for row in table.rows:
                for idx, w in enumerate(col_widths):
                    row.cells[idx].width = Inches(w)

        p_sep = doc.add_paragraph()
        p_sep.paragraph_format.space_before = Pt(3)
        p_sep.paragraph_format.space_after = Pt(3)

    # --- ENCABEZADOS Y PIES DE PÁGINA ---
    header = section.header
    p_head = header.paragraphs[0]
    p_head.alignment = WD_ALIGN_PARAGRAPH.RIGHT
    run_head = p_head.add_run("IIICCD — Universidad Nacional del Altiplano | Manual de Usuario Oficial")
    run_head.font.name = "Arial"
    run_head.font.size = Pt(8)
    run_head.font.color.rgb = COLOR_TEXTO_MUTED

    footer = section.footer
    p_foot = footer.paragraphs[0]
    p_foot.alignment = WD_ALIGN_PARAGRAPH.LEFT
    run_foot = p_foot.add_run("Sistema Web de Gestión Académica y Certificación Digital con QR — Edición 2026")
    run_foot.font.name = "Arial"
    run_foot.font.size = Pt(8)
    run_foot.font.color.rgb = COLOR_TEXTO_MUTED

    # =========================================================================
    # 1. PORTADA INSTITUCIONAL DE GALA
    # =========================================================================
    p_inst = doc.add_paragraph()
    p_inst.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p_inst.paragraph_format.space_before = Pt(8)
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
    r_inst.font.size = Pt(9.5)
    r_inst.font.bold = True
    r_inst.font.color.rgb = COLOR_ORO

    # Logo Centrado
    if os.path.exists(logo_path):
        p_logo = doc.add_paragraph()
        p_logo.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p_logo.paragraph_format.space_before = Pt(12)
        p_logo.paragraph_format.space_after = Pt(12)
        p_logo.add_run().add_picture(logo_path, width=Inches(1.8))

    # Título Principal del Manual
    p_tit = doc.add_paragraph()
    p_tit.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p_tit.paragraph_format.space_before = Pt(6)
    p_tit.paragraph_format.space_after = Pt(4)
    r_tit = p_tit.add_run("MANUAL DE USUARIO Y OPERACIONES DEL SISTEMA WEB DE GESTIÓN ACADÉMICA, INSCRIPCIONES, PASARELA YAPE Y CERTIFICACIÓN OFICIAL CON CÓDIGO QR")
    r_tit.font.name = "Arial"
    r_tit.font.size = Pt(13)
    r_tit.font.bold = True
    r_tit.font.color.rgb = COLOR_GRANATE

    # Subtítulo
    p_sub = doc.add_paragraph()
    p_sub.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p_sub.paragraph_format.space_before = Pt(0)
    p_sub.paragraph_format.space_after = Pt(16)
    r_sub = p_sub.add_run("Guía Oficial Ilustrada Paso a Paso para Participantes, Docentes y Directiva Institucional\n(Con Capturas de Pantalla Reales de la Plataforma en Producción)")
    r_sub.font.name = "Arial"
    r_sub.font.size = Pt(10)
    r_sub.font.italic = True
    r_sub.font.color.rgb = COLOR_AZUL_OSCURO

    # Ficha Técnica en Tabla
    tbl_meta = doc.add_table(rows=6, cols=2)
    tbl_meta.alignment = WD_TABLE_ALIGNMENT.CENTER
    tbl_meta.autofit = False
    configurar_bordes_tabla(tbl_meta)

    meta_info = [
        ("Sistema de Software:", "Plataforma Web de Gestión de Cursos y Certificación Oficial"),
        ("Entidad Titular:", "Instituto de Investigación en Inteligencia Computacional y Ciencia de Datos (IIICCD)"),
        ("Dependencia Universitaria:", "Facultad de Ingeniería Estadística e Informática — UNA Puno"),
        ("Versión de Software / Despliegue:", "Versión 1.0.0 Enterprise • Cloud Deployment Activo"),
        ("URL Oficial de Producción:", "https://cursos-sistema.onrender.com"),
        ("Fecha de Publicación:", "Octubre de 2026")
    ]
    for idx, (lbl, val) in enumerate(meta_info):
        c0, c1 = tbl_meta.rows[idx].cells
        c0.width = Inches(2.2)
        c1.width = Inches(4.0)
        c0.text = lbl
        c1.text = val
        aplicar_sombreado(c0, COLOR_FONDO_CLARO_HEX)
        aplicar_sombreado(c1, "FFFFFF")
        aplicar_margenes_celda(c0, top=70, bottom=70, left=100, right=100)
        aplicar_margenes_celda(c1, top=70, bottom=70, left=100, right=100)
        p0 = c0.paragraphs[0]
        p0.runs[0].font.name = "Arial"
        p0.runs[0].font.size = Pt(8.5)
        p0.runs[0].font.bold = True
        p0.runs[0].font.color.rgb = COLOR_AZUL_OSCURO
        p1 = c1.paragraphs[0]
        p1.runs[0].font.name = "Arial"
        p1.runs[0].font.size = Pt(8.5)
        p1.runs[0].font.color.rgb = COLOR_TEXTO

    doc.add_page_break()

    # =========================================================================
    # 2. ÍNDICE GENERAL DEL MANUAL
    # =========================================================================
    add_h1("ÍNDICE GENERAL DEL MANUAL")
    add_p("La presente guía está estructurada en secciones independientes orientadas al perfil de cada usuario, permitiendo localizar rápidamente instrucciones operativas para inscripciones, pagos, emisión de certificados y administración general.")

    tbl_ind = doc.add_table(rows=16, cols=3)
    tbl_ind.alignment = WD_TABLE_ALIGNMENT.CENTER
    tbl_ind.autofit = False
    configurar_bordes_tabla(tbl_ind)

    indice_data = [
        ("Sección I", "Introducción, Arquitectura de Acceso y Roles del Sistema", "Módulo 1 - 2"),
        ("Sección II", "GUÍA DEL PARTICIPANTE: Navegación, Matrícula y Aula Virtual", "Módulo 3 - 8"),
        ("• 2.1", "Exploración de la Portada y Búsqueda de Cursos", "Figura 1 - 2"),
        ("• 2.2", "Catálogo de Cursos, Filtros por Línea y Tarifas UNA Puno", "Figura 3 - 4"),
        ("• 2.3", "Registro de Nuevo Usuario con DNI y Teléfono Celular", "Figura 5"),
        ("• 2.4", "Autenticación Segura (Login Split-Screen)", "Figura 6"),
        ("• 2.5", "Pasarela Yape, Código de Operación y Subida de Comprobante", "Instrucciones"),
        ("• 2.6", "Panel Personal («Mis Cursos»), Enlace Zoom/Meet y WhatsApp", "Figura 7"),
        ("• 2.7", "Visualización, Descarga de Diploma PDF 300 DPI y LinkedIn", "Figura 8"),
        ("Sección III", "GUÍA DEL ADMINISTRADOR: Control Académico, Pagos y Certificación", "Módulo 9 - 16"),
        ("• 3.1", "Dashboard Administrativo y Métricas en Tiempo Real", "Figura 9"),
        ("• 3.2", "Gestión de Cursos, Docentes Investigadores y Paginación", "Figura 10 - 11"),
        ("• 3.3", "Estudio Gráfico y Generador de Flyers Oficiales del IIICCD", "Figura 12"),
        ("• 3.4", "Bandeja de Aprobación de Pagos y Auditoría de Vouchers Yape", "Figura 13"),
        ("• 3.5", "Directorio de Alumnos con Filtro Desplegable y Enlace WhatsApp", "Figura 14"),
        ("• 3.6", "Bandeja de Mensajes, Configuración Yape/QR y Emisión Oficial", "Figura 15 - 17"),
    ]

    for idx, (c_sec, c_tit, c_ref) in enumerate(indice_data):
        r = tbl_ind.rows[idx]
        r.cells[0].width = Inches(1.2)
        r.cells[1].width = Inches(4.0)
        r.cells[2].width = Inches(1.0)
        r.cells[0].text = c_sec
        r.cells[1].text = c_tit
        r.cells[2].text = c_ref
        shd = COLOR_FONDO_CLARO_HEX if (idx % 2 == 1) else "FFFFFF"
        for c in r.cells:
            aplicar_sombreado(c, shd)
            aplicar_margenes_celda(c, top=60, bottom=60, left=80, right=80)
            p = c.paragraphs[0]
            p.runs[0].font.name = "Arial"
            p.runs[0].font.size = Pt(8.5)
            p.runs[0].font.color.rgb = COLOR_AZUL_OSCURO if c_sec.startswith("Sección") else COLOR_TEXTO
            if c_sec.startswith("Sección"):
                p.runs[0].font.bold = True

    add_p("", space_after=10)

    # =========================================================================
    # SECCIÓN I: INTRODUCCIÓN Y ARQUITECTURA DE ACCESO
    # =========================================================================
    add_h1("SECCIÓN I: INTRODUCCIÓN Y ARQUITECTURA DE ACCESO")
    add_p("El Sistema Web de Cursos y Certificación Oficial del Instituto de Investigación en Inteligencia Computacional y Ciencia de Datos (IIICCD - FINESI - UNA Puno) es una plataforma digital de nivel enterprise desarrollada bajo arquitectura limpia en Spring Boot 3, Java 17 y PostgreSQL 16. Ha sido diseñada con la finalidad de centralizar, automatizar y asegurar el ciclo completo de formación continua e investigación.")

    add_h2("1.1. Perfiles de Usuario y Niveles de Acceso")
    add_p("La plataforma implementa un control de acceso basado en roles (Role-Based Access Control - RBAC) blindado por Spring Security 6:")
    add_bullet("Cualquier estudiante universitario, investigador, egresado o profesional externo. Puede explorar el catálogo, matricularse, subir vouchers de Yape, ingresar a las clases virtuales y acceder a sus diplomas oficiales.", "1. Perfil Participante (Estudiante): ")
    add_bullet("Miembros acreditados de la directiva del IIICCD (Director, Secretario Académico, Coordinadores). Disponen de privilegios para crear programas, revisar comprobantes bancarios, generar afiches publicitarios, emitir certificados con valor curricular y configurar los canales de cobro.", "2. Perfil Administrador (Directiva): ")
    add_bullet("Cualquier evaluador, empleador o entidad que consulte la autenticidad de un diploma en /verificar sin necesidad de tener cuenta.", "3. Visitante / Público General: ")

    add_h2("1.2. Requisitos de Acceso y Compatibilidad")
    add_p("Al tratarse de una solución Cloud moderna basada en web, el sistema no requiere instalación de software en los equipos de los usuarios:")
    add_bullet("Cualquier navegador web con soporte HTML5 (Google Chrome, Microsoft Edge, Mozilla Firefox, Safari u Opera) tanto en computadoras de escritorio, laptops, tablets o smartphones.", "Navegador Web: ")
    add_bullet("Conexión básica a internet (3G, 4G, 5G o fibra óptica). La aplicación cuenta con compresión GZIP y optimización de caché local que permite operar fluidamente con baja conectividad.", "Conectividad: ")
    add_bullet("https://cursos-sistema.onrender.com (con certificado de seguridad SSL/TLS activo).", "Dirección Web Oficial: ")

    # =========================================================================
    # SECCIÓN II: GUÍA DEL PARTICIPANTE
    # =========================================================================
    add_h1("SECCIÓN II: GUÍA DEL PARTICIPANTE (ESTUDIANTE / INVESTIGADOR)")
    add_p("Esta sección detalla paso a paso las operaciones que realiza un estudiante desde su llegada al portal hasta la obtención y descarga de su certificado universitario.")

    add_h2("2.1. Exploración del Portal Institucional")
    add_p("Al ingresar a la dirección web principal, el usuario accede a una interfaz institucional moderna con identidad visual de la Universidad Nacional del Altiplano:")
    add_step(1, "Ingreso a la Portada", "Abra su navegador e ingrese a la dirección web oficial. Visualizará la barra de navegación superior con efecto de cristal (Glassmorphism), el Hero institucional con estadísticas activas y el botón directo para explorar las convocatorias.")
    add_step(2, "Convocatoria Destacada (Flyer Oficial)", "En la parte superior se exhibe en una vitrina flotante el afiche oficial de gala del curso más reciente, con fecha de inicio, horarios, créditos y botón directo para ver el afiche en alta resolución.")

    add_figure("fig01_inicio_portal.png", "Figura 1: Portal Principal del Sistema, Hero Institucional y Barra de Navegación con Glassmorphism.")
    add_figure("fig02_inicio_flyer.png", "Figura 2: Vitrina Flotante Institucional con el Afiche Promocional del Programa Destacado.")

    add_h2("2.2. Catálogo Oficial de Cursos y Selección de Modalidad")
    add_p("En el menú superior haga clic en «Cursos» o navegue a /catalogo para ingresar al catálogo completo:")
    add_step(1, "Búsqueda Interactiva", "Escriba palabras clave (ej. 'Machine Learning', 'Algoritmos', 'Python') en el buscador superior. La lista se filtrará en tiempo real sin recargar la página.")
    add_step(2, "Filtro por Línea de Investigación", "Haga clic en las pestañas superiores para ver únicamente cursos de Ciencias de la Computación, Ciencia de Datos o Metodologías de Investigación.")
    add_step(3, "Identificación de Tarifas y Vacantes", "Cada tarjeta expone insignias dinámicas que alertan sobre vacantes abiertas o últimos cupos, así como la tarifa diferenciada para la Comunidad Universitaria UNA Puno frente al Público General.")

    add_figure("fig03_catalogo_cursos.png", "Figura 3: Catálogo General de Cursos con Buscador Instantáneo y Pastillas de Estado.")

    add_h2("2.3. Ficha Académica del Curso y Consulta de Requisitos")
    add_p("Al hacer clic en cualquier curso, se presenta la ficha técnica completa:")
    add_bullet("Horas lectivas certificadas y créditos académicos universitarios con valor formal.", "Acreditación: ")
    add_bullet("Tarjeta del ponente con su fotografía oficial, grado académico e institución de procedencia.", "Docente Investigador: ")
    add_bullet("Enfoque temático, herramientas de software a emplear y perfil del egresado.", "Contenido del Curso: ")
    add_bullet("Inversión para estudiantes/docentes UNA Puno vs. Público General.", "Tarifas Transparentes: ")

    add_figure("fig04_curso_detalle.png", "Figura 4: Ficha Académica de Detalle del Curso con Perfil del Ponente y Credenciales de Honor.")

    add_h2("2.4. Registro de Nuevo Usuario y Validaciones de Seguridad")
    add_p("Para inscribirse por primera vez, el participante debe crear una cuenta en el sistema:")
    add_step(1, "Acceso al Formulario", "Haga clic en el botón «Registrarse» ubicado en la esquina superior derecha.")
    add_step(2, "DNI Restringido a 8 Dígitos", "Ingrese su Documento Nacional de Identidad. El sistema valida en tiempo real que contenga exactamente 8 números, bloqueando letras o caracteres no válidos.")
    add_step(3, "Teléfono / WhatsApp a 9 Dígitos", "Ingrese su número de teléfono celular peruano (exactamente 9 dígitos numéricos). Este número es fundamental, ya que el sistema lo utilizará para enviarle sus accesos y diplomas por WhatsApp.")
    add_step(4, "Contraseña y Confirmación", "Cree una contraseña segura. El formulario cuenta con un botón de visualización rápida (icono de ojo) para verificar los caracteres ingresados antes de enviar.")

    add_figure("fig05_registro_usuario.png", "Figura 5: Formulario de Registro con Reglas Estrictas de DNI (8 dígitos) y Celular (9 dígitos).")

    add_h2("2.5. Inicio de Sesión Seguro (Login Split-Screen)")
    add_p("Una vez registrado, ingrese a /login:")
    add_step(1, "Ingreso de Credenciales", "Escriba su correo electrónico registrado y contraseña.")
    add_step(2, "Entorno de Confianza", "La pantalla de inicio de sesión incorpora los pilares institucionales del IIICCD y el escudo de la Facultad de Ingeniería Estadística e Informática.")
    add_step(3, "Redirección Inteligente", "Al iniciar sesión, el sistema detecta su rol y lo dirige automáticamente a su panel personal («Mis Cursos») si es estudiante, o al Panel Administrativo si es directivo.")

    add_figure("fig06_login_acceso.png", "Figura 6: Pantalla de Autenticación Split-Screen con Pilares Institucionales de Seguridad.")

    add_h2("2.6. Proceso de Pago con Yape y Subida de Comprobante")
    add_p("Al pulsar «Inscribirme Ahora» en un curso, el sistema lo guía en el proceso de pago seguro:")
    add_step(1, "Escaneo del QR de Yape", "Se presenta en pantalla el código QR oficial del instituto y el número telefónico del titular de la cuenta.")
    add_step(2, "Realización del Yape", "Desde su teléfono celular abra la aplicación Yape, escanee el QR o envíe al número y verifique el nombre del titular antes de confirmar.")
    add_step(3, "Subida del Comprobante", "Capture la pantalla del voucher emitido por Yape. El sistema incluye un compresor automático en cliente que reduce la imagen a ~250KB en 50 milisegundos sin pérdida de calidad, permitiendo una carga instantánea.")
    add_step(4, "Código de Operación", "Escriba el número de operación que figura en su voucher de Yape y pulse «Confirmar y Enviar Comprobante». La inscripción pasará al estado PENDIENTE_VERIFICACION mientras la directiva revisa el abono.")

    add_callout(
        "El sistema bloquea automáticamente cualquier intento de doble inscripción activa para el mismo curso. "
        "Si su pago está en revisión o ya fue aprobado, la plataforma protege su matrícula impidiendo cobros duplicados.",
        "IMPORTANTE: PROTECCIÓN CONTRA DOBLE PAGO",
        COLOR_ORO_HEX
    )

    add_h2("2.7. Panel del Participante («Mis Cursos») y Aula Virtual")
    add_p("En el menú superior, el botón de cursos se transforma automáticamente en «Mis Cursos» (/participante/dashboard). En este panel personal el estudiante dispone de:")
    add_bullet("Muestra claramente si su comprobante está en revisión, si su matrícula está aprobada o si el curso ha concluido.", "Insignia de Estado: ")
    add_bullet("Al ser aprobado su pago, se desbloquea el botón destacado para ingresar a la sala de Zoom o Google Meet.", "Acceso a la Sesión en Vivo: ")
    add_bullet("Enlace protegido para unirse al grupo de mensajería oficial del curso con el docente y compañeros.", "Comunidad de WhatsApp: ")
    add_bullet("Una vez que la directiva marca el curso como culminado, aparecen los botones oficiales para acceder al diploma.", "Acciones de Certificado: ")

    add_figure("fig07_participante_dashboard.png", "Figura 7: Panel Personal del Participante («Mis Cursos») con Enlaces Protegidos a Clases y Certificados.")

    add_h2("2.8. Visualización, Descarga de Diploma PDF 300 DPI y LinkedIn")
    add_p("Cuando la inscripción adquiere el estado COMPLETADA, el participante dispone de dos opciones de máxima calidad:")
    add_step(1, "Diploma Digital Interactivo (Ver Certificado)", "Abre una pantalla de gala con doble orla Granate UNA y Oro Académico, marca de agua del isotipo en alta definición, firmas de Decanatura y Dirección, y código QR vectorial.")
    add_step(2, "Descargar PDF Oficial", "Genera y descarga en 1 segundo el documento PDF vectorial a 300 DPI con motor OpenPDF, listo para imprimir en cartulina o adjuntar en legajos de convocatorias.")
    add_step(3, "Añadir a LinkedIn", "Un botón configurado con los parámetros de la API de LinkedIn permite anexar la certificación oficial con el código único de validación a su perfil profesional con un solo clic.")
    add_step(4, "Recibir por WhatsApp", "Permite solicitar la constancia digital directamente al teléfono oficial del IIICCD con un mensaje pre-redactado.")

    add_figure("fig08_participante_certificado.png", "Figura 8: Diploma Digital Oficial de Honor con Orla Universitaria, Isotipo Vectorial y Código QR.")

    doc.add_page_break()

    # =========================================================================
    # SECCIÓN III: GUÍA DE LA DIRECTIVA Y ADMINISTRACIÓN
    # =========================================================================
    add_h1("SECCIÓN III: GUÍA DE LA DIRECTIVA Y ADMINISTRACIÓN")
    add_p("Esta sección describe en detalle los módulos de gestión, control de matrículas, diseño de afiches, verificación de pagos bancarios y emisión de certificados a disposición de la directiva del IIICCD.")

    add_h2("3.1. Panel de Control Administrativo (Dashboard)")
    add_p("Al autenticarse con credenciales de administrador e ingresar a /admin, se presenta la consola de mando institucional:")
    add_bullet("Cursos Activos, Pagos por Verificar, Inscripciones Aprobadas y Certificados Emitidos.", "Cuadrícula de Métricas Principales: ")
    add_bullet("Acceso directo con contador de no leídos a la bandeja de consultas (/admin/mensajes).", "Bandeja de Mensajes: ")
    add_bullet("Acceso directo al padrón general de estudiantes (/admin/usuarios).", "Directorio de Alumnos: ")
    add_bullet("Botones de un clic para crear cursos, diseñar flyers o ajustar los parámetros de Yape.", "Acciones Rápidas: ")

    add_figure("fig10_admin_dashboard.png", "Figura 9: Panel de Control del Administrador con Métricas y Accesos Directos.")

    add_h2("3.2. Gestión Integral de Cursos y Paginación Institucional")
    add_p("Al ingresar a /admin/cursos, el administrador visualiza el padrón completo de programas académicos:")
    add_step(1, "Navegación Paginada a 4 Cursos", "Para garantizar fluidez y orden visual, la tabla muestra exactamente 4 cursos por página con controles numéricos de paginación [ 1 ] [ 2 ] y botones Anterior/Siguiente.")
    add_step(2, "Switch de Portada (Publicar Flyer)", "Una columna dedicada permite activar o desactivar qué afiche se proyectará en la vitrina flotante de la página de inicio con un solo clic.")
    add_step(3, "Creación de Nuevo Curso", "El botón «+ Nuevo Curso» abre el formulario exhaustivo (/admin/cursos/nuevo) donde se definen:")
    add_bullet("Nombre formal, línea de investigación (Ciencias de la Computación, Ciencia de Datos o Investigación) y área especializada.", "Datos Académicos: ")
    add_bullet("Carga de fotografía profesional del ponente y cargo/especialidad académica.", "Perfil Docente: ")
    add_bullet("Horas lectivas certificadas y número de créditos académicos formales.", "Valor Curricular: ")
    add_bullet("Precio general y precio bonificado para la comunidad UNA Puno.", "Tarifario Diferenciado: ")
    add_bullet("Enlace privado a la sala virtual (Zoom / Google Meet) y enlace al grupo exclusivo de WhatsApp.", "Canales Virtuales: ")

    add_figure("fig11_admin_cursos_lista.png", "Figura 10: Tabla de Gestión de Cursos con Paginación a 4 Registros y Acciones Rápidas.")
    add_figure("fig12_admin_curso_nuevo.png", "Figura 11: Formulario de Creación y Edición de Cursos con Carga de Fotografía del Docente.")

    add_h2("3.3. Estudio Gráfico y Generador de Flyers Oficiales (/admin/flyer)")
    add_p("La plataforma incorpora una herramienta de diseño gráfico en vivo diseñada a medida para el IIICCD:")
    add_step(1, "Diseño en Tiempo Real", "Permite seleccionar cualquier curso registrado para importar automáticamente sus datos, o redactar textos libres.")
    add_step(2, "Temas Cromáticos Institucionales", "Conmutador instantáneo entre Granate UNA Puno (Oficial), Cyber Deep IA (Azul Noche) y Esmeralda Ciencia de Datos.")
    add_step(3, "Formatos de Salida", "Soporte para proporción vertical (4:5 para estados de WhatsApp e historias) y cuadrado (1:1 para publicaciones).")
    add_step(4, "Descarga en Ultra HD", "Botón «Descargar PNG (Alta Resolución)» que exporta el afiche a 2.5x con html2canvas, listo para imprenta o redes sociales.")

    add_figure("fig13_admin_flyer_studio.png", "Figura 12: Estudio de Diseño y Generador de Flyers Oficiales del IIICCD.")

    add_h2("3.4. Bandeja de Revisión y Auditoría de Pagos Yape (/admin/inscripciones/pendientes)")
    add_p("Cada vez que un participante sube un voucher, se encola en la bandeja de verificación:")
    add_step(1, "Inspección del Comprobante", "Al hacer clic en «Revisar Voucher», se despliega el comprobante original en alta resolución servido por Cloudinary.")
    add_step(2, "Cotejo Bancario", "El administrador verifica que el código de operación, monto y titular coincidan con los registros del celular de tesorería.")
    add_step(3, "Aprobación Inmediata", "Al pulsar «✓ Aprobar Pago», el sistema reserva formalmente la vacante, habilita los accesos al Zoom/WhatsApp en el panel del alumno y despacha un correo de confirmación.")
    add_step(4, "Rechazo con Retroalimentación", "Si el comprobante es ilegible o el monto es incorrecto, el administrador puede rechazar la operación indicando el motivo para que el alumno regularice.")

    add_figure("fig14_admin_pagos_pendientes.png", "Figura 13: Bandeja de Revisión de Comprobantes Yape con Código de Operación y Modal de Auditoría.")

    add_h2("3.5. Directorio de Alumnos con Filtro Desplegable (/admin/usuarios)")
    add_p("Módulo integral para la gestión y prospección de estudiantes registrados:")
    add_bullet("Permite filtrar en 1 clic entre 'Todos los Registrados', 'Sin cursos aún' (oportunidad comercial) o seleccionar un curso específico.", "Menú Desplegable por Curso: ")
    add_bullet("Pastilla superior que informa el filtro activo con opción de limpieza rápida.", "Indicador Visual: ")
    add_bullet("Cada participante cuenta con un botón verde que abre directamente WhatsApp Web o la App con su número normalizado (+51) para brindarle asistencia.", "Contacto Directo por WhatsApp: ")

    add_figure("fig15_admin_usuarios_dropdown.png", "Figura 14: Directorio de Usuarios con Filtro Desplegable de Cursos y Contacto por WhatsApp.")

    add_h2("3.6. Bandeja de Consultas de Contacto (/admin/mensajes)")
    add_p("Centraliza los requerimientos enviados a través del formulario público de /contacto:")
    add_bullet("Filas no leídas resaltadas con borde azul sutil al estilo de clientes de correo modernos.", "Indicadores Visuales: ")
    add_bullet("Botón directo para abrir cliente de correo con el destinatario y asunto institucional prellenado.", "Respuesta por Correo: ")
    add_bullet("Si el usuario ingresó teléfono celular, permite responderle de inmediato a su WhatsApp.", "Respuesta por WhatsApp: ")

    add_figure("fig16_admin_mensajes_consultas.png", "Figura 15: Bandeja de Mensajes de Contacto y Módulo de Respuesta Multicanal.")

    add_h2("3.7. Configuración Dinámica de Cobros (/admin/config/pago)")
    add_p("Permite actualizar los datos de recaudación sin necesidad de reiniciar el servidor ni tocar código:")
    add_bullet("Modificación del número de celular receptor de Yape.", "Número de Teléfono: ")
    add_bullet("Actualización del nombre del titular responsable ante la SUNAT y la universidad.", "Titular de la Cuenta: ")
    add_bullet("Subida de una nueva imagen de código QR que se sincroniza al instante en la nube de Cloudinary.", "Código QR Oficial: ")

    add_figure("fig17_admin_config_pago.png", "Figura 16: Configuración Dinámica de Parámetros de Recaudación y Código QR de Yape.")

    add_h2("3.8. Emisión de Certificados y Envío Directo por WhatsApp")
    add_p("Al finalizar las clases de un curso, el administrador ingresa al detalle del curso (/admin/cursos/{id}):")
    add_step(1, "Marcado de Curso Completado", "Junto a cada alumno aprobado hace clic en «✓ Marcar Curso Completado».")
    add_step(2, "Generación Inmutable", "El sistema toma una instantánea fotográfica inmutable de los datos del alumno, horas, créditos y docente, asignándole el código único irrepetible (ej. IIICCD-8A7F2D).")
    add_step(3, "Envío por WhatsApp en 1 Clic", "En la tabla de certificados (/admin/certificados), el administrador pulsa «📲 Enviar Certificado por WhatsApp». Se abrirá WhatsApp con un mensaje formal de felicitación y el enlace directo para que el estudiante descargue su diploma.")

    add_figure("fig18_admin_certificados_lista.png", "Figura 17: Padrón de Certificados Emitidos y Botón de Envío Directo por WhatsApp.")

    doc.add_page_break()

    # =========================================================================
    # SECCIÓN IV: VERIFICACIÓN PÚBLICA Y SEGURIDAD
    # =========================================================================
    add_h1("SECCIÓN IV: VERIFICACIÓN PÚBLICA Y SEGURIDAD ANTI-FRAUDE")
    add_p("La autenticidad de los diplomas emitidos por el IIICCD está respaldada por un sistema de validación criptográfico accesible universalmente.")

    add_h2("4.1. Verificador Universal en Línea (/verificar)")
    add_p("Cualquier persona, empleador, comité de selección o institución académica puede verificar un certificado sin necesidad de tener usuario ni contraseña:")
    add_step(1, "Acceso a la Consulta", "Ingrese a https://cursos-sistema.onrender.com/verificar.")
    add_step(2, "Ingreso del Código Único", "Escriba el código alfanumérico que figura en la esquina inferior del documento (ej. IIICCD-7B3E91).")
    add_step(3, "Escaneo del Código QR", "Si se dispone de un teléfono inteligente, basta con enfocar la cámara al código QR impreso en el diploma; el navegador abrirá de inmediato la ficha oficial.")
    add_step(4, "Ficha de Autenticidad", "La pantalla despliega el medallón dorado oficial de validación, confirmando el nombre del titular, documento de identidad, curso aprobado, horas académicas, créditos y fecha de expedición.")

    add_figure("fig09_verificador_publico.png", "Figura 18: Portal de Verificación Pública con Medallón Dorado y Certificación Inmutable.")
    add_figure("fig19_quienes_somos.png", "Figura 19: Portal Institucional de Identidad Académica y Directiva Oficial del IIICCD.")

    # =========================================================================
    # SECCIÓN V: PREGUNTAS FRECUENTES Y SOPORTE
    # =========================================================================
    add_h1("SECCIÓN V: PREGUNTAS FRECUENTES Y SOPORTE TÉCNICO")
    add_p("A continuación se recopilan las dudas operativas más comunes y las soluciones recomendadas:")

    faq_data = [
        ("¿Qué sucede si subí un comprobante de Yape erróneo?", 
         "La directiva marcará su inscripción como RECHAZADA indicando la observación. En su panel de «Mis Cursos» volverá a habilitarse el botón «Subir Voucher Yape» para que adjunte el comprobante correcto sin perder su cuenta."),
        ("¿Por qué no puedo ver el enlace de Zoom/Meet?", 
         "Los enlaces de clase en vivo están protegidos por seguridad. Solo se vuelven visibles una vez que la directiva ha validado su voucher y su estado pasa a APROBADA."),
        ("¿Puedo descargar mi certificado desde mi teléfono móvil?", 
         "Sí. El botón «Descargar PDF» funciona tanto en computadoras como en teléfonos celulares Android o iOS, generando el archivo oficial a 300 DPI."),
        ("¿Qué validez tienen los créditos académicos del certificado?", 
         "Son emitidos con respaldo formal del IIICCD y la Facultad de Ingeniería Estadística e Informática de la UNA Puno, consignando horas lectivas y valor curricular oficial."),
        ("¿Cómo recupero mi contraseña si la olvidé?", 
         "Comuníquese a través de la sección de Contacto (/contacto) o directamente al canal oficial de WhatsApp del instituto proporcionando su DNI registrado para el restablecimiento seguro de credenciales.")
    ]

    for p_q, p_a in faq_data:
        add_callout(p_a, "• " + p_q, COLOR_AZUL_OSCURO_HEX)

    # Bloque de Cierre y Firmas
    add_h2("Conformidad y Cierre de Entrega")
    add_p("El presente Manual de Usuario y Operación documenta con exactitud el comportamiento, arquitectura y flujo del sistema entregado formalmente al Instituto de Investigación en Inteligencia Computacional y Ciencia de Datos (IIICCD) de la Universidad Nacional del Altiplano de Puno.")

    # Tabla de Firmas
    tbl_sig = doc.add_table(rows=1, cols=2)
    tbl_sig.alignment = WD_TABLE_ALIGNMENT.CENTER
    tbl_sig.autofit = False

    c0, c1 = tbl_sig.rows[0].cells
    c0.width = Inches(3.1)
    c1.width = Inches(3.1)

    p_f0 = c0.paragraphs[0]
    p_f0.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p_f0.add_run("_________________________________________\n").font.color.rgb = COLOR_AZUL_OSCURO
    r_f0_name = p_f0.add_run("DIRECCIÓN DEL IIICCD\n")
    r_f0_name.font.name = "Arial"
    r_f0_name.font.bold = True
    r_f0_name.font.size = Pt(9.5)
    r_f0_name.font.color.rgb = COLOR_AZUL_OSCURO
    r_f0_cargo = p_f0.add_run("Instituto de Investigación en Inteligencia Computacional y Ciencia de Datos\nFINESI — UNA Puno")
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

    # Guardar documento
    try:
        doc.save(output_docx_path)
        print(f"Manual de usuario generado exitosamente en: {output_docx_path}")
        print(f"Tamaño del archivo Word generado: {os.path.getsize(output_docx_path)} bytes")
    except PermissionError:
        alt_path = os.path.join(base_dir, "informe", "Manual_de_Usuario_Sistema_Cursos_IIICCD_Actualizado.docx")
        doc.save(alt_path)
        print(f"AVISO: Archivo principal abierto, guardado en: {alt_path}")
        print(f"Tamaño del archivo Word generado: {os.path.getsize(alt_path)} bytes")

if __name__ == "__main__":
    build_manual_usuario()
