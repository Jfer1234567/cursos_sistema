// main.js - Aceleración de Navegación, Prefetch Predictivo e Interacciones Base

document.addEventListener('DOMContentLoaded', () => {
    // 1. Menú móvil responsive
    const menuToggle = document.querySelector('.menu-toggle');
    const mainNav = document.querySelector('.main-nav');

    if (menuToggle && mainNav) {
        menuToggle.addEventListener('click', () => {
            mainNav.classList.toggle('show');
        });
    }

    // 2. Auto-ocultar alertas flash después de 6 segundos
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.transition = 'opacity 0.5s ease';
            alert.style.opacity = '0';
            setTimeout(() => alert.remove(), 500);
        }, 6000);
    });

    // 3. Barra de Navegación Instantánea Superior (Top-Bar Progress estilo GitHub/Linear)
    let progressBar = document.getElementById('instant-progress-bar');
    if (!progressBar) {
        progressBar = document.createElement('div');
        progressBar.id = 'instant-progress-bar';
        progressBar.style.cssText = 'position:fixed;top:0;left:0;height:3px;width:0%;background:linear-gradient(90deg, #6D1A24, #C99738);z-index:999999;transition:width 0.2s ease, opacity 0.3s ease;pointer-events:none;box-shadow:0 0 8px rgba(201,151,56,0.6);';
        document.body.appendChild(progressBar);
    }

    function startProgress() {
        progressBar.style.opacity = '1';
        progressBar.style.width = '30%';
        setTimeout(() => { progressBar.style.width = '75%'; }, 150);
    }

    // 4. Pre-carga Predictiva Inteligente (Instant Prefetch al Hover)
    const prefetchedUrls = new Set();
    prefetchedUrls.add(window.location.href);

    function prefetchUrl(url) {
        if (!url || prefetchedUrls.has(url)) return;
        try {
            const parsed = new URL(url, window.location.origin);
            if (parsed.origin !== window.location.origin) return; // solo enlaces internos
            if (parsed.pathname.includes('/logout') || parsed.pathname.includes('/pdf') || parsed.pathname.includes('/descargar')) return;
            if (parsed.pathname.endsWith('.pdf') || parsed.pathname.endsWith('.png') || parsed.pathname.endsWith('.jpg')) return;

            prefetchedUrls.add(url);
            const link = document.createElement('link');
            link.rel = 'prefetch';
            link.href = url;
            link.as = 'document';
            document.head.appendChild(link);
        } catch (e) {
            // Ignorar URLs inválidas
        }
    }

    // Escuchar hover y touch en todos los enlaces internos
    document.addEventListener('mouseover', (e) => {
        const a = e.target.closest('a');
        if (a && a.href && !a.target && !a.hasAttribute('download')) {
            prefetchUrl(a.href);
        }
    }, { passive: true });

    document.addEventListener('touchstart', (e) => {
        const a = e.target.closest('a');
        if (a && a.href && !a.target && !a.hasAttribute('download')) {
            prefetchUrl(a.href);
        }
    }, { passive: true });

    // Activar barra de progreso en clicks de navegación interna
    document.addEventListener('click', (e) => {
        const a = e.target.closest('a');
        if (a && a.href && !a.target && !a.hasAttribute('download') && !a.href.startsWith('javascript:') && !a.href.includes('#')) {
            const parsed = new URL(a.href, window.location.origin);
            if (parsed.origin === window.location.origin && parsed.pathname !== window.location.pathname) {
                startProgress();
            }
        }
    });

    // 5. Feedback instantáneo y protección anti-doble envío en formularios
    document.addEventListener('submit', (e) => {
        const form = e.target;
        if (form && !form.classList.contains('no-instant-feedback')) {
            startProgress();
            const btn = form.querySelector('button[type="submit"], input[type="submit"]');
            if (btn && !btn.disabled) {
                // Darle feedback visual inmediato al usuario
                setTimeout(() => {
                    btn.style.opacity = '0.75';
                    btn.style.cursor = 'wait';
                }, 50);
            }
        }
    });
});
