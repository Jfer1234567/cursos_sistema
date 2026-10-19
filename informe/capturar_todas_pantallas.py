import os
import time
from playwright.sync_api import sync_playwright

BASE_URL = "https://cursos-sistema.onrender.com"
OUTPUT_DIR = os.path.abspath(os.path.join(os.path.dirname(__file__), "capturas"))

def capturar():
    os.makedirs(OUTPUT_DIR, exist_ok=True)
    
    with sync_playwright() as p:
        browser = p.chromium.launch(
            executable_path=r"C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe",
            headless=True
        )
        
        # Contexto 1: Público (Sin autenticar)
        context_pub = browser.new_context(viewport={"width": 1366, "height": 820})
        page = context_pub.new_page()
        
        print("1. Capturando Inicio Portal...")
        page.goto(f"{BASE_URL}/", wait_until="domcontentloaded")
        page.wait_for_timeout(2500)
        page.screenshot(path=os.path.join(OUTPUT_DIR, "fig01_inicio_portal.png"))
        
        print("2. Capturando Inicio Sección Flyer / Convocatorias...")
        page.evaluate("window.scrollTo(0, 550)")
        page.wait_for_timeout(1000)
        page.screenshot(path=os.path.join(OUTPUT_DIR, "fig02_inicio_flyer.png"))
        
        print("3. Capturando Catálogo de Cursos...")
        page.goto(f"{BASE_URL}/catalogo", wait_until="domcontentloaded")
        page.wait_for_timeout(2000)
        page.screenshot(path=os.path.join(OUTPUT_DIR, "fig03_catalogo_cursos.png"))
        
        # Intentar obtener el enlace del primer curso para detalle
        curso_link = page.query_selector("a[href*='/cursos/']")
        if curso_link:
            curso_href = curso_link.get_attribute("href")
            print(f"4. Capturando Ficha de Detalle del Curso ({curso_href})...")
            page.goto(f"{BASE_URL}{curso_href}" if curso_href.startswith("/") else curso_href, wait_until="domcontentloaded")
            page.wait_for_timeout(2000)
            page.screenshot(path=os.path.join(OUTPUT_DIR, "fig04_curso_detalle.png"))
        else:
            print("4. Capturando Líneas de Investigación como detalle...")
            page.goto(f"{BASE_URL}/lineas-investigacion", wait_until="domcontentloaded")
            page.wait_for_timeout(1500)
            page.screenshot(path=os.path.join(OUTPUT_DIR, "fig04_curso_detalle.png"))

        print("5. Capturando Formulario de Registro...")
        page.goto(f"{BASE_URL}/registro", wait_until="domcontentloaded")
        page.wait_for_timeout(1500)
        page.screenshot(path=os.path.join(OUTPUT_DIR, "fig05_registro_usuario.png"))
        
        print("6. Capturando Inicio de Sesión (Login)...")
        page.goto(f"{BASE_URL}/login", wait_until="domcontentloaded")
        page.wait_for_timeout(1500)
        page.screenshot(path=os.path.join(OUTPUT_DIR, "fig06_login_acceso.png"))
        
        print("7. Capturando Verificador Público...")
        page.goto(f"{BASE_URL}/verificar", wait_until="domcontentloaded")
        page.wait_for_timeout(1500)
        page.screenshot(path=os.path.join(OUTPUT_DIR, "fig09_verificador_publico.png"))
        
        print("8. Capturando Quiénes Somos...")
        page.goto(f"{BASE_URL}/quienes-somos", wait_until="domcontentloaded")
        page.wait_for_timeout(1500)
        page.screenshot(path=os.path.join(OUTPUT_DIR, "fig19_quienes_somos.png"))
        
        context_pub.close()

        # Contexto 2: Administrador
        print("\n--- Autenticando como Administrador ---")
        context_admin = browser.new_context(viewport={"width": 1366, "height": 820})
        page_admin = context_admin.new_page()
        page_admin.goto(f"{BASE_URL}/login", wait_until="domcontentloaded")
        page_admin.fill("input[name='correo']", "admin@iiiccd.edu.pe")
        page_admin.fill("input[name='password']", "admin123")
        page_admin.click("button[type='submit']")
        page_admin.wait_for_timeout(3500)
        
        print("9. Capturando Panel Admin (Dashboard)...")
        page_admin.goto(f"{BASE_URL}/admin", wait_until="domcontentloaded")
        page_admin.wait_for_timeout(2000)
        page_admin.screenshot(path=os.path.join(OUTPUT_DIR, "fig10_admin_dashboard.png"))
        
        print("10. Capturando Gestión de Cursos (/admin/cursos)...")
        page_admin.goto(f"{BASE_URL}/admin/cursos", wait_until="domcontentloaded")
        page_admin.wait_for_timeout(2000)
        page_admin.screenshot(path=os.path.join(OUTPUT_DIR, "fig11_admin_cursos_lista.png"))
        
        print("11. Capturando Formulario Nuevo Curso (/admin/cursos/nuevo)...")
        page_admin.goto(f"{BASE_URL}/admin/cursos/nuevo", wait_until="domcontentloaded")
        page_admin.wait_for_timeout(2000)
        page_admin.screenshot(path=os.path.join(OUTPUT_DIR, "fig12_admin_curso_nuevo.png"))
        
        print("12. Capturando Generador de Flyers (/admin/flyer)...")
        page_admin.goto(f"{BASE_URL}/admin/flyer", wait_until="domcontentloaded")
        page_admin.wait_for_timeout(2500)
        page_admin.screenshot(path=os.path.join(OUTPUT_DIR, "fig13_admin_flyer_studio.png"))
        
        print("13. Capturando Bandeja de Pagos Pendientes (/admin/inscripciones/pendientes)...")
        page_admin.goto(f"{BASE_URL}/admin/inscripciones/pendientes", wait_until="domcontentloaded")
        page_admin.wait_for_timeout(2000)
        page_admin.screenshot(path=os.path.join(OUTPUT_DIR, "fig14_admin_pagos_pendientes.png"))
        
        print("14. Capturando Directorio de Usuarios (/admin/usuarios)...")
        page_admin.goto(f"{BASE_URL}/admin/usuarios", wait_until="domcontentloaded")
        page_admin.wait_for_timeout(2000)
        page_admin.screenshot(path=os.path.join(OUTPUT_DIR, "fig15_admin_usuarios_dropdown.png"))
        
        print("15. Capturando Bandeja de Mensajes (/admin/mensajes)...")
        page_admin.goto(f"{BASE_URL}/admin/mensajes", wait_until="domcontentloaded")
        page_admin.wait_for_timeout(2000)
        page_admin.screenshot(path=os.path.join(OUTPUT_DIR, "fig16_admin_mensajes_consultas.png"))
        
        print("16. Capturando Configuración de Pago (/admin/config/pago)...")
        page_admin.goto(f"{BASE_URL}/admin/config/pago", wait_until="domcontentloaded")
        page_admin.wait_for_timeout(2000)
        page_admin.screenshot(path=os.path.join(OUTPUT_DIR, "fig17_admin_config_pago.png"))
        
        print("17. Capturando Certificados Emitidos (/admin/certificados)...")
        page_admin.goto(f"{BASE_URL}/admin/certificados", wait_until="domcontentloaded")
        page_admin.wait_for_timeout(2000)
        page_admin.screenshot(path=os.path.join(OUTPUT_DIR, "fig18_admin_certificados_lista.png"))
        
        # Si hay algún certificado en la lista, abrir la vista previa
        cert_link = page_admin.query_selector("a[href*='/participante/certificado/']")
        if cert_link:
            cert_href = cert_link.get_attribute("href")
            print(f"18. Capturando Diploma Digital ({cert_href})...")
            page_admin.goto(f"{BASE_URL}{cert_href}" if cert_href.startswith("/") else cert_href, wait_until="domcontentloaded")
            page_admin.wait_for_timeout(2500)
            page_admin.screenshot(path=os.path.join(OUTPUT_DIR, "fig08_participante_certificado.png"))
            
        context_admin.close()

        # Contexto 3: Participante
        print("\n--- Autenticando como Participante ---")
        context_user = browser.new_context(viewport={"width": 1366, "height": 820})
        page_user = context_user.new_page()
        page_user.goto(f"{BASE_URL}/login", wait_until="domcontentloaded")
        page_user.fill("input[name='correo']", "participante@iiiccd.edu.pe")
        page_user.fill("input[name='password']", "participante123")
        page_user.click("button[type='submit']")
        page_user.wait_for_timeout(3500)
        
        print("19. Capturando Dashboard del Participante (/participante/dashboard)...")
        page_user.goto(f"{BASE_URL}/participante/dashboard", wait_until="domcontentloaded")
        page_user.wait_for_timeout(2000)
        page_user.screenshot(path=os.path.join(OUTPUT_DIR, "fig07_participante_dashboard.png"))
        
        # Si aún no teníamos la foto del diploma, buscarla en el dashboard
        if not os.path.exists(os.path.join(OUTPUT_DIR, "fig08_participante_certificado.png")):
            user_cert = page_user.query_selector("a[href*='/participante/certificado/']")
            if user_cert:
                cert_href = user_cert.get_attribute("href")
                print(f"20. Capturando Diploma desde Dashboard ({cert_href})...")
                page_user.goto(f"{BASE_URL}{cert_href}" if cert_href.startswith("/") else cert_href, wait_until="domcontentloaded")
                page_user.wait_for_timeout(2500)
                page_user.screenshot(path=os.path.join(OUTPUT_DIR, "fig08_participante_certificado.png"))
        
        context_user.close()
        browser.close()
        
    print("\n¡Todas las capturas se generaron exitosamente en:", OUTPUT_DIR)

if __name__ == "__main__":
    capturar()
