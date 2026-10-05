import os
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
import matplotlib.patches as patches

def draw_table(ax, x, y, width, height, table_name, pk_list, fk_list, attr_list, header_color="#6D1A24", bg_color="#FFFFFF"):
    """Dibuja una entidad de base de datos estilo UML / Crow's Foot."""
    # Sombra sutil
    shadow = patches.FancyBboxPatch((x + 0.08, y - 0.08), width, height,
                                   boxstyle="round,pad=0.04,rounding_size=0.15",
                                   facecolor="#CBD5E1", edgecolor="none", alpha=0.5, zorder=1)
    ax.add_patch(shadow)

    # Contenedor principal de la tabla
    box = patches.FancyBboxPatch((x, y), width, height,
                                 boxstyle="round,pad=0.04,rounding_size=0.15",
                                 facecolor=bg_color, edgecolor="#6D1A24", linewidth=1.5, zorder=2)
    ax.add_patch(box)

    # Cabecera de la tabla
    header_h = 0.55
    header_box = patches.Rectangle((x, y + height - header_h), width, header_h,
                                  facecolor=header_color, edgecolor="none", zorder=3)
    ax.add_patch(header_box)

    # Título de la tabla
    ax.text(x + width / 2.0, y + height - header_h / 2.0, table_name,
            ha='center', va='center', color='white', fontweight='bold', fontsize=9.5, fontfamily='sans-serif', zorder=4)

    # Línea divisoria debajo de la cabecera
    ax.plot([x, x + width], [y + height - header_h, y + height - header_h], color="#C99738", linewidth=1.8, zorder=4)

    # Listar atributos
    curr_y = y + height - header_h - 0.22
    line_h = 0.23

    # 1. Primary Keys
    for col, col_type in pk_list:
        ax.text(x + 0.12, curr_y, "[PK]", ha='left', va='center', color='#C99738', fontweight='bold', fontsize=7.5, fontfamily='monospace', zorder=4)
        ax.text(x + 0.55, curr_y, col, ha='left', va='center', color='#1E293B', fontweight='bold', fontsize=8, fontfamily='sans-serif', zorder=4)
        ax.text(x + width - 0.12, curr_y, col_type, ha='right', va='center', color='#64748B', fontsize=7.5, fontfamily='monospace', zorder=4)
        curr_y -= line_h

    # 2. Foreign Keys
    for col, col_type in fk_list:
        ax.text(x + 0.12, curr_y, "[FK]", ha='left', va='center', color='#0284C7', fontweight='bold', fontsize=7.5, fontfamily='monospace', zorder=4)
        ax.text(x + 0.55, curr_y, col, ha='left', va='center', color='#0F172A', fontweight='semibold', fontsize=8, fontfamily='sans-serif', zorder=4)
        ax.text(x + width - 0.12, curr_y, col_type, ha='right', va='center', color='#64748B', fontsize=7.5, fontfamily='monospace', zorder=4)
        curr_y -= line_h

    # 3. Regular Attributes
    for col, col_type in attr_list:
        ax.text(x + 0.12, curr_y, " •", ha='left', va='center', color='#94A3B8', fontsize=7.5, zorder=4)
        ax.text(x + 0.40, curr_y, col, ha='left', va='center', color='#334155', fontsize=8, fontfamily='sans-serif', zorder=4)
        ax.text(x + width - 0.12, curr_y, col_type, ha='right', va='center', color='#64748B', fontsize=7.5, fontfamily='monospace', zorder=4)
        curr_y -= line_h

def draw_connector(ax, p1, p2, label1="1", label2="N", color="#6D1A24", rad=0.0):
    """Traza línea de relación con etiquetas de cardinalidad."""
    ax.annotate("", xy=p2, xytext=p1,
                arrowprops=dict(arrowstyle="-|>", color=color, lw=1.5,
                               connectionstyle=f"arc3,rad={rad}"), zorder=5)
    
    # Texto de cardinalidad
    ax.text(p1[0] + 0.1, p1[1] + 0.1, label1, color=color, fontweight='bold', fontsize=8.5, zorder=6)
    ax.text(p2[0] - 0.2, p2[1] + 0.1, label2, color=color, fontweight='bold', fontsize=8.5, zorder=6)

def generate_database_diagram():
    fig, ax = plt.subplots(figsize=(17, 11), dpi=300)
    ax.set_xlim(0, 17)
    ax.set_ylim(0, 11)
    ax.axis('off')

    # Fondo decorativo tenue
    fig.patch.set_facecolor('#F8FAFC')
    ax.set_facecolor('#F8FAFC')

    # Título institucional en la parte superior
    ax.text(8.5, 10.6, "MODELO RELACIONAL DE BASE DE DATOS (POSTGRESQL 16)",
            ha='center', va='center', color='#6D1A24', fontweight='bold', fontsize=14, fontfamily='sans-serif')
    ax.text(8.5, 10.25, "Instituto de Investigación en Inteligencia Computacional y Ciencia de Datos — UNA Puno",
            ha='center', va='center', color='#C99738', fontweight='semibold', fontsize=10, fontfamily='sans-serif')

    # --- FILA SUPERIOR: ESTRUCTURA ACADÉMICA ---
    # 1. lineas_investigacion
    draw_table(ax, 0.6, 7.3, 3.4, 2.3, "lineas_investigacion",
               [("id", "BIGSERIAL")],
               [],
               [("nombre", "VARCHAR(150)"),
                ("descripcion", "TEXT"),
                ("orden", "INTEGER"),
                ("color_hex", "VARCHAR(20)"),
                ("activo", "BOOLEAN")])

    # 2. areas_investigacion
    draw_table(ax, 4.8, 7.3, 3.4, 2.3, "areas_investigacion",
               [("id", "BIGSERIAL")],
               [("linea_investigacion_id", "BIGINT")],
               [("nombre", "VARCHAR(150)"),
                ("descripcion", "TEXT"),
                ("activo", "BOOLEAN")])

    # 3. cursos
    draw_table(ax, 9.0, 5.7, 3.8, 3.9, "cursos",
               [("id", "BIGSERIAL")],
               [("area_investigacion_id", "BIGINT")],
               [("nombre", "VARCHAR(200)"),
                ("descripcion_corta", "VARCHAR(255)"),
                ("docente_responsable", "VARCHAR(150)"),
                ("docente_cargo", "VARCHAR(150)"),
                ("docente_foto_url", "VARCHAR(255)"),
                ("precio", "NUMERIC(10,2)"),
                ("precio_comunidad", "NUMERIC(10,2)"),
                ("cupos_totales", "INTEGER"),
                ("cupos_disponibles", "INTEGER"),
                ("estado", "VARCHAR(20)"),
                ("enlace_clase", "VARCHAR(255)"),
                ("enlace_whatsapp", "VARCHAR(255)"),
                ("creditos", "INTEGER"),
                ("flyer_url", "VARCHAR(255)"),
                ("flyer_publicado", "BOOLEAN")])

    # --- FILA INTERMEDIA: ACTORES Y MATRÍCULA ---
    # 4. usuarios
    draw_table(ax, 0.6, 3.6, 3.4, 3.1, "usuarios",
               [("id", "BIGSERIAL")],
               [],
               [("correo", "VARCHAR(150)"),
                ("password", "VARCHAR(255)"),
                ("nombre_completo", "VARCHAR(150)"),
                ("tipo_documento", "VARCHAR(20)"),
                ("numero_documento", "VARCHAR(20)"),
                ("telefono", "VARCHAR(20)"),
                ("institucion", "VARCHAR(150)"),
                ("rol", "VARCHAR(20)"),
                ("activo", "BOOLEAN"),
                ("created_at", "TIMESTAMP")])

    # 5. inscripciones
    draw_table(ax, 4.8, 3.8, 3.4, 2.7, "inscripciones",
               [("id", "BIGSERIAL")],
               [("usuario_id", "BIGINT"),
                ("curso_id", "BIGINT")],
               [("estado", "VARCHAR(30)"),
                ("fecha_inscripcion", "TIMESTAMP"),
                ("notas_admin", "VARCHAR(255)"),
                ("created_at", "TIMESTAMP")])

    # --- FILA INFERIOR: FINANZAS Y CERTIFICACIÓN ---
    # 6. pagos
    draw_table(ax, 9.0, 1.4, 3.8, 3.5, "pagos",
               [("id", "BIGSERIAL")],
               [("inscripcion_id", "BIGINT"),
                ("verificado_por_id", "BIGINT")],
               [("monto", "NUMERIC(10,2)"),
                ("metodo_pago", "VARCHAR(20)"),
                ("referencia_externa", "VARCHAR(50)"),
                ("comprobante_url", "VARCHAR(255)"),
                ("comprobante_nombre_orig", "VARCHAR(255)"),
                ("estado", "VARCHAR(20)"),
                ("fecha_pago", "TIMESTAMP"),
                ("fecha_verificacion", "TIMESTAMP")])

    # 7. certificados
    draw_table(ax, 0.6, 0.4, 3.6, 2.7, "certificados",
               [("id", "BIGSERIAL")],
               [("inscripcion_id", "BIGINT")],
               [("codigo_unico", "VARCHAR(50)"),
                ("nombre_estudiante", "VARCHAR(150)"),
                ("documento_estudiante", "VARCHAR(20)"),
                ("nombre_curso", "VARCHAR(200)"),
                ("docente", "VARCHAR(150)"),
                ("creditos", "INTEGER"),
                ("fecha_emision", "TIMESTAMP"),
                ("url_verificacion", "VARCHAR(255)")])

    # 8. configuracion_pago (Configuración de Cobros)
    draw_table(ax, 4.8, 0.6, 3.4, 2.2, "configuracion_pago",
               [("id", "BIGSERIAL")],
               [],
               [("numero_yape", "VARCHAR(20)"),
                ("titular_yape", "VARCHAR(150)"),
                ("qr_imagen_url", "VARCHAR(255)"),
                ("instrucciones", "TEXT"),
                ("updated_at", "TIMESTAMP")],
               header_color="#1E293B")

    # 9. mensajes_contacto (Bandeja de Consultas)
    draw_table(ax, 13.3, 3.8, 3.2, 2.7, "mensajes_contacto",
               [("id", "BIGSERIAL")],
               [],
               [("nombre_remitente", "VARCHAR(150)"),
                ("correo", "VARCHAR(150)"),
                ("telefono", "VARCHAR(20)"),
                ("asunto", "VARCHAR(200)"),
                ("mensaje", "TEXT"),
                ("fecha_envio", "TIMESTAMP"),
                ("leido", "BOOLEAN"),
                ("respondido", "BOOLEAN")],
               header_color="#1E293B")

    # --- LÍNEAS DE RELACIÓN Y CARDINALIDADES ---
    # lineas -> areas
    draw_connector(ax, (4.0, 8.45), (4.8, 8.45), "1", "N")

    # areas -> cursos
    draw_connector(ax, (8.2, 8.45), (9.0, 8.45), "1", "N")

    # cursos -> inscripciones (curva hacia abajo)
    draw_connector(ax, (9.0, 6.2), (8.2, 5.5), "1", "N", rad=-0.1)

    # usuarios -> inscripciones
    draw_connector(ax, (4.0, 5.2), (4.8, 5.2), "1", "N")

    # inscripciones -> pagos
    draw_connector(ax, (8.2, 4.2), (9.0, 3.5), "1", "1", rad=0.0)

    # inscripciones -> certificados
    draw_connector(ax, (4.8, 4.0), (3.6, 2.9), "1", "1", rad=0.1)

    # usuarios (admin) -> pagos (verificado_por)
    draw_connector(ax, (4.0, 3.7), (9.0, 2.0), "1", "N", color="#0284C7", rad=-0.25)

    # Leyenda de relaciones
    leg_x, leg_y = 13.3, 8.0
    leg_w, leg_h = 3.2, 1.8
    leg_box = patches.FancyBboxPatch((leg_x, leg_y), leg_w, leg_h,
                                    boxstyle="round,pad=0.03,rounding_size=0.1",
                                    facecolor="#FFFFFF", edgecolor="#CBD5E1", lw=1.2)
    ax.add_patch(leg_box)
    ax.text(leg_x + 0.15, leg_y + leg_h - 0.25, "LEYENDA RELACIONAL", fontweight='bold', fontsize=8.5, color='#6D1A24')
    ax.plot([leg_x + 0.15, leg_x + 0.6], [leg_y + leg_h - 0.6, leg_y + leg_h - 0.6], color="#6D1A24", lw=2)
    ax.text(leg_x + 0.75, leg_y + leg_h - 0.6, "Relación 1:N / 1:1", fontsize=8, color='#334155', va='center')
    ax.plot([leg_x + 0.15, leg_x + 0.6], [leg_y + leg_h - 0.95, leg_y + leg_h - 0.95], color="#0284C7", lw=2)
    ax.text(leg_x + 0.75, leg_y + leg_h - 0.95, "Auditoría Admin (FK)", fontsize=8, color='#334155', va='center')
    ax.text(leg_x + 0.15, leg_y + leg_h - 1.35, "[PK] Llave Primaria\n[FK] Llave Foránea", fontsize=7.5, color='#64748B')

    # Guardar imagen en alta resolución
    output_img = os.path.join(os.path.dirname(__file__), "diagrama_base_datos.png")
    plt.tight_layout()
    plt.savefig(output_img, dpi=300, bbox_inches='tight', facecolor=fig.get_facecolor(), edgecolor='none')
    plt.close()
    print(f"Diagrama de base de datos generado exitosamente en: {output_img}")

if __name__ == "__main__":
    generate_database_diagram()
