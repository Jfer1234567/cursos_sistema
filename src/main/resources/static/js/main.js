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

    // 2. Caché en Memoria RAM del Navegador para Vistas Públicas (0 ms por clic) con TTL de 30s
    const pageCache = new Map();
    const CACHE_TTL_MS = 30000; // 30 segundos

    // Determinar si una ruta es protegida (requiere autenticación y nunca debe cachearse en memoria)
    function isProtectedPath(url) {
        if (!url) return false;
        try {
            const u = new URL(url, window.location.origin);
            return u.pathname.startsWith('/admin') || 
                   u.pathname.startsWith('/participante') || 
                   u.pathname.startsWith('/inscripcion');
        } catch (e) {
            return false;
        }
    }

    function getFromCache(url) {
        if (isProtectedPath(url)) return null;
        const item = pageCache.get(url);
        if (!item) return null;
        if (Date.now() - item.time > CACHE_TTL_MS) {
            pageCache.delete(url);
            return null;
        }
        return item.htmlText;
    }

    function setInCache(url, htmlText) {
        if (isProtectedPath(url)) return;
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

    // Precargar página completa en memoria RAM al hover o touch (solo rutas públicas)
    async function prefetchToMemory(url) {
        if (!url || isProtectedPath(url) || getFromCache(url)) return;
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
            // Si la respuesta fue redirigida al login (ej. sesión caducada o logout)
            if (res.redirected || (res.url && res.url.includes('/login') && !url.includes('/login'))) {
                window.location.href = res.url || '/login';
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
                executeScripts(currentMain);
            }, 40);
        } else {
            // Si una de las páginas es de diseño atípico (ej. login puro o 404), swap completo
            document.body.innerHTML = doc.body.innerHTML;
            window.scrollTo({ top: 0, behavior: 'instant' });
            initInteractiveComponents();
            executeScripts(document.body);
        }
    }

    // Re-ejecutar scripts dinámicos incluidos en páginas intercambiadas por SPA
    function executeScripts(container) {
        if (!container) return;
        const scripts = Array.from(container.querySelectorAll('script'));
        scripts.forEach(oldScript => {
            if (oldScript.src && oldScript.src.includes('main.js')) return;
            const newScript = document.createElement('script');
            Array.from(oldScript.attributes).forEach(attr => newScript.setAttribute(attr.name, attr.value));
            if (oldScript.src) {
                newScript.src = oldScript.src;
            } else {
                newScript.textContent = oldScript.textContent;
            }
            oldScript.parentNode.replaceChild(newScript, oldScript);
        });
    }

    // Manejar botón Atrás / Adelante del navegador instantáneamente
    window.addEventListener('popstate', (e) => {
        const url = window.location.href;
        // Las rutas protegidas nunca se sirven de caché: siempre validan con el servidor
        if (isProtectedPath(url)) {
            navigateInstant(url, false);
            return;
        }
        const cachedHtml = getFromCache(url);
        if (cachedHtml) {
            renderPage(cachedHtml, url, false);
        } else {
            navigateInstant(url, false);
        }
    });

    // Detección de BFCache (Back-Forward Cache): si el navegador intenta revivir una página protegida desde RAM
    window.addEventListener('pageshow', (event) => {
        const isBfCache = event.persisted || 
            (window.performance && window.performance.getEntriesByType && 
             window.performance.getEntriesByType('navigation')[0]?.type === 'back_forward');
        if (isBfCache && isProtectedPath(window.location.href)) {
            // Forzar recarga real desde el servidor para que Spring Security verifique autenticación
            window.location.reload();
        }
    });

    // Purgar caché en memoria al enviar cualquier formulario de cierre de sesión
    document.addEventListener('submit', (e) => {
        const form = e.target;
        if (form && form.action && form.action.includes('/logout')) {
            pageCache.clear();
            try {
                sessionStorage.clear();
            } catch (err) {}
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

    // 3. Manejo Global y Resiliente de Menús Desplegables (.dropdown-btn de 3 puntos)
    window.toggleCourseDropdown = function(btn, e) {
        if (e) {
            e.preventDefault();
            e.stopPropagation();
        }
        const container = btn.closest('.dropdown-actions-container');
        const menu = container ? container.querySelector('.course-dropdown-menu') : null;
        if (!menu) return;

        const isVisible = menu.style.display === 'block';
        document.querySelectorAll('.course-dropdown-menu').forEach(m => {
            m.style.display = 'none';
        });

        if (!isVisible) {
            menu.style.display = 'block';
        }
    };

    // Delegación global de clics para menús desplegables (.dropdown-btn)
    document.addEventListener('click', (e) => {
        const btn = e.target.closest('.dropdown-btn');
        if (btn) {
            window.toggleCourseDropdown(btn, e);
            return;
        }

        // Clic fuera del contenedor: cerrar todos los menús desplegables
        if (!e.target.closest('.dropdown-actions-container')) {
            document.querySelectorAll('.course-dropdown-menu').forEach(m => {
                m.style.display = 'none';
            });
        }
    });

    // Cerrar cualquier menú desplegable al presionar Escape
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') {
            document.querySelectorAll('.course-dropdown-menu').forEach(m => {
                m.style.display = 'none';
            });
        }
    });

    // 4. Inicializador de Componentes Interactivos (Re-ejecutable tras navegación SPA)
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
