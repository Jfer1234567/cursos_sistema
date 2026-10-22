// main.js - Motor de Navegación Instantánea SPA, Prefetch en Memoria y Aceleración Base

(function() {
    'use strict';

    // 1. Barra de Navegación Instantánea Superior (Top-Bar Progress)
    let progressBar = null;
    function initProgressBar() {
        if (!progressBar) {
            progressBar = document.getElementById('instant-progress-bar');
            if (!progressBar) {
                progressBar = document.createElement('div');
                progressBar.id = 'instant-progress-bar';
                progressBar.style.cssText = 'position:fixed;top:0;left:0;height:3px;width:0%;background:linear-gradient(90deg, #6D1A24, #C99738);z-index:999999;transition:width 0.15s ease, opacity 0.25s ease;pointer-events:none;box-shadow:0 0 10px rgba(201,151,56,0.7);';
                document.body.appendChild(progressBar);
            }
        }
    }

    let progressTimer = null;
    function startProgress() {
        initProgressBar();
        clearTimeout(progressTimer);
        progressBar.style.opacity = '1';
        progressBar.style.width = '35%';
        progressTimer = setTimeout(() => {
            if (progressBar.style.opacity === '1') {
                progressBar.style.width = '80%';
            }
        }, 120);
    }

    function finishProgress() {
        if (!progressBar) return;
        clearTimeout(progressTimer);
        progressBar.style.width = '100%';
        setTimeout(() => {
            progressBar.style.opacity = '0';
            setTimeout(() => {
                progressBar.style.width = '0%';
            }, 250);
        }, 80);
    }

    // 2. Caché en Memoria RAM del Navegador para Vistas (0 ms por clic) con TTL de 30s
    const pageCache = new Map();
    const CACHE_TTL_MS = 30000; // 30 segundos

    function getFromCache(url) {
        const item = pageCache.get(url);
        if (!item) return null;
        if (Date.now() - item.time > CACHE_TTL_MS) {
            pageCache.delete(url);
            return null;
        }
        return item.htmlText;
    }

    function setInCache(url, htmlText) {
        pageCache.set(url, { htmlText, time: Date.now() });
    }

    function isEligibleLink(a) {
        if (!a || !a.href || a.target || a.hasAttribute('download')) return false;
        const href = a.getAttribute('href');
        if (!href || href.startsWith('#') || href.startsWith('javascript:') || href.startsWith('mailto:') || href.startsWith('tel:')) return false;
        try {
            const url = new URL(a.href, window.location.origin);
            if (url.origin !== window.location.origin) return false;
            // Excluir rutas de descarga o acciones no aptas para swap SPA
            if (url.pathname.includes('/logout') || url.pathname.includes('/pdf') || url.pathname.includes('/descargar')) return false;
            if (url.pathname.endsWith('.pdf') || url.pathname.endsWith('.png') || url.pathname.endsWith('.jpg') || url.pathname.endsWith('.jpeg')) return false;
            if (url.pathname.includes('/admin/flyer')) return false; // Editor especializado
            return true;
        } catch (e) {
            return false;
        }
    }

    // Actualizar enlaces activos en la cabecera institucional
    function updateActiveNavLinks(targetUrl) {
        try {
            const urlObj = new URL(targetUrl, window.location.origin);
            const pathname = urlObj.pathname;
            document.querySelectorAll('.site-header .nav-link').forEach(link => {
                const href = link.getAttribute('href');
                if (!href) return;
                if (href === pathname || (href !== '/' && pathname.startsWith(href))) {
                    link.classList.add('active');
                } else {
                    link.classList.remove('active');
                }
            });
        } catch (e) {}
    }

    // Precargar página completa en memoria RAM al hover o touch
    async function prefetchToMemory(url) {
        if (!url || getFromCache(url)) return;
        try {
            const res = await fetch(url, { headers: { 'X-Requested-With': 'SPA-Prefetch' } });
            if (res.ok) {
                const htmlText = await res.text();
                setInCache(url, htmlText);
            }
        } catch (e) {
            // Silencioso
        }
    }

    // Navegar instantáneamente intercambiando el contenido en el DOM
    async function navigateInstant(url, pushState = true) {
        startProgress();

        const cachedHtml = getFromCache(url);
        if (cachedHtml) {
            renderPage(cachedHtml, url, pushState);
            finishProgress();
            return;
        }

        try {
            const res = await fetch(url, { headers: { 'X-Requested-With': 'SPA-Fetch' } });
            if (!res.ok) {
                window.location.href = url;
                return;
            }
            const htmlText = await res.text();
            setInCache(url, htmlText);
            renderPage(htmlText, url, pushState);
            finishProgress();
        } catch (e) {
            window.location.href = url;
        }
    }

    // Renderizado granular: preserva cabecera institucional y pie de página estables sin parpadeos
    function renderPage(htmlText, url, pushState) {
        const parser = new DOMParser();
        const doc = parser.parseFromString(htmlText, 'text/html');
        const title = doc.querySelector('title')?.innerText || document.title;
        document.title = title;

        if (pushState) {
            window.history.pushState({ url }, title, url);
        }

        const currentMain = document.querySelector('main.main-content');
        const newMain = doc.querySelector('main.main-content');

        if (currentMain && newMain) {
            // Actualizar alertas del sistema
            const currentAlerts = document.getElementById('global-alerts-container');
            const newAlerts = doc.getElementById('global-alerts-container');
            if (currentAlerts && newAlerts) {
                currentAlerts.innerHTML = newAlerts.innerHTML;
            }

            // Transición suave y ultrarrápida del contenido central (0-40 ms)
            currentMain.style.opacity = '0';
            currentMain.style.transform = 'translateY(3px)';
            currentMain.style.transition = 'opacity 0.08s ease-out, transform 0.08s ease-out';

            setTimeout(() => {
                currentMain.innerHTML = newMain.innerHTML;
                currentMain.style.opacity = '1';
                currentMain.style.transform = 'translateY(0)';
                updateActiveNavLinks(url);
                window.scrollTo({ top: 0, behavior: 'instant' });
                initInteractiveComponents();
            }, 40);
        } else {
            // Si una de las páginas es de diseño atípico (ej. login puro o 404), swap completo
            document.body.innerHTML = doc.body.innerHTML;
            window.scrollTo({ top: 0, behavior: 'instant' });
            initInteractiveComponents();
        }
    }

    // Manejar botón Atrás / Adelante del navegador instantáneamente
    window.addEventListener('popstate', (e) => {
        const url = window.location.href;
        const cachedHtml = getFromCache(url);
        if (cachedHtml) {
            renderPage(cachedHtml, url, false);
        } else {
            navigateInstant(url, false);
        }
    });

    // Delegación global de clicks en enlaces
    document.addEventListener('click', (e) => {
        const a = e.target.closest('a');
        if (a && isEligibleLink(a)) {
            const url = a.href;
            if (url !== window.location.href) {
                e.preventDefault();
                navigateInstant(url, true);
            }
        }
    });

    // Delegación de hover y touchstart para prefetch predictivo
    document.addEventListener('mouseover', (e) => {
        const a = e.target.closest('a');
        if (a && isEligibleLink(a)) {
            prefetchToMemory(a.href);
        }
    }, { passive: true });

    document.addEventListener('touchstart', (e) => {
        const a = e.target.closest('a');
        if (a && isEligibleLink(a)) {
            prefetchToMemory(a.href);
        }
    }, { passive: true });

    // 3. Inicializador de Componentes Interactivos (Re-ejecutable tras navegación SPA)
    function initInteractiveComponents() {
        initProgressBar();

        // Guardar la página actual en caché con TTL
        if (!getFromCache(window.location.href)) {
            setInCache(window.location.href, document.documentElement.outerHTML);
        }

        // A. Menú móvil responsive con drawer, animación a 'X' y telón táctil
        const menuToggle = document.querySelector('.menu-toggle');
        const mainNav = document.querySelector('.main-nav');
        const backdrop = document.getElementById('mobile-nav-backdrop');

        function closeMobileNav() {
            if (mainNav) mainNav.classList.remove('show');
            if (menuToggle) menuToggle.classList.remove('active');
            if (backdrop) backdrop.classList.remove('show');
            document.body.style.overflow = '';
        }

        function toggleMobileNav() {
            if (!mainNav || !menuToggle) return;
            const isOpen = mainNav.classList.toggle('show');
            menuToggle.classList.toggle('active', isOpen);
            if (backdrop) backdrop.classList.toggle('show', isOpen);
            document.body.style.overflow = isOpen ? 'hidden' : '';
        }

        if (menuToggle) {
            menuToggle.onclick = (e) => {
                e.stopPropagation();
                toggleMobileNav();
            };
        }

        if (backdrop) {
            backdrop.onclick = closeMobileNav;
        }

        // Cerrar menú al hacer clic en enlaces dentro del nav
        if (mainNav) {
            mainNav.querySelectorAll('a, button').forEach(el => {
                el.addEventListener('click', () => {
                    closeMobileNav();
                });
            });
        }

        // Cerrar menú con tecla ESC o si se redimensiona a pantalla ancha
        window.addEventListener('keydown', (e) => {
            if (e.key === 'Escape') closeMobileNav();
        });

        window.addEventListener('resize', () => {
            if (window.innerWidth > 992) {
                closeMobileNav();
            }
        });

        // B. Auto-ocultar alertas flash después de 5 segundos
        document.querySelectorAll('.alert').forEach(alert => {
            setTimeout(() => {
                alert.style.transition = 'opacity 0.4s ease, transform 0.4s ease';
                alert.style.opacity = '0';
                alert.style.transform = 'translateY(-6px)';
                setTimeout(() => alert.remove(), 400);
            }, 5000);
        });

        // C. Autocompresión en cliente de fotos de comprobantes (HTML5 Canvas)
        const fileInputs = document.querySelectorAll('input[type="file"][name="comprobante"]');
        fileInputs.forEach(input => {
            input.onchange = function(evt) {
                const file = evt.target.files[0];
                if (!file || !file.type.startsWith('image/')) return;

                // Si pesa más de 300KB, comprimirla en 40ms en el navegador
                if (file.size > 300 * 1024) {
                    const reader = new FileReader();
                    reader.onload = function(e) {
                        const img = new Image();
                        img.onload = function() {
                            const canvas = document.createElement('canvas');
                            let width = img.width;
                            let height = img.height;
                            const maxDimension = 1600;

                            if (width > height && width > maxDimension) {
                                height = Math.round((height * maxDimension) / width);
                                width = maxDimension;
                            } else if (height > maxDimension) {
                                width = Math.round((width * maxDimension) / height);
                                height = maxDimension;
                            }

                            canvas.width = width;
                            canvas.height = height;
                            const ctx = canvas.getContext('2d');
                            ctx.drawImage(img, 0, 0, width, height);

                            canvas.toBlob(function(blob) {
                                if (blob && blob.size < file.size) {
                                    const compressedFile = new File([blob], file.name.replace(/\.[^/.]+$/, ".jpg"), {
                                        type: 'image/jpeg',
                                        lastModified: Date.now()
                                    });
                                    const dataTransfer = new DataTransfer();
                                    dataTransfer.items.add(compressedFile);
                                    input.files = dataTransfer.files;

                                    let badge = input.parentElement.querySelector('.compressed-badge');
                                    if (!badge) {
                                        badge = document.createElement('div');
                                        badge.className = 'compressed-badge';
                                        badge.style.cssText = 'color:#059669;font-size:0.8rem;font-weight:600;margin-top:0.35rem;';
                                        input.parentElement.appendChild(badge);
                                    }
                                    const kbOriginal = Math.round(file.size / 1024);
                                    const kbNuevo = Math.round(compressedFile.size / 1024);
                                    badge.innerHTML = `✓ Voucher optimizado automáticamente: ${kbOriginal} KB &rarr; ${kbNuevo} KB (subida instantánea)`;
                                }
                            }, 'image/jpeg', 0.85);
                        };
                        img.src = e.target.result;
                    };
                    reader.readAsDataURL(file);
                }
            };
        });

        // D. Feedback visual instantáneo y protección anti-doble envío en formularios
        document.querySelectorAll('form').forEach(form => {
            if (form.dataset.instantBound) return;
            form.dataset.instantBound = 'true';

            form.addEventListener('submit', function() {
                startProgress();
                const btn = form.querySelector('button[type="submit"], input[type="submit"]');
                if (btn && !btn.disabled) {
                    setTimeout(() => {
                        btn.disabled = true;
                        btn.style.opacity = '0.85';
                        const origText = btn.innerHTML;
                        btn.innerHTML = `<span class="btn-spinner"></span> Procesando...`;
                        // Fallback de reactivación tras 10 segundos
                        setTimeout(() => {
                            btn.disabled = false;
                            btn.innerHTML = origText;
                        }, 10000);
                    }, 20);
                }
            });
        });
    }

    // 4. Arranque Inicial
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initInteractiveComponents);
    } else {
        initInteractiveComponents();
    }

})();
