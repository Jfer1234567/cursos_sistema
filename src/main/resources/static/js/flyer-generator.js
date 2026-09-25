/**
 * IIICCD - Generador y Editor de Flyers Oficiales
 * Reactividad en tiempo real y exportación en alta resolución PNG
 */
document.addEventListener('DOMContentLoaded', function () {
    const canvas = document.getElementById('flyerCanvas');
    if (!canvas) return;

    // Elementos de Entrada
    const selectCursoCargar = document.getElementById('selectCursoCargar');
    const inputTheme = document.getElementById('inputTheme');
    const inputFormat = document.getElementById('inputFormat');

    const inputTitulo = document.getElementById('inputTitulo');
    const inputSubtitulo = document.getElementById('inputSubtitulo');
    const inputLinea = document.getElementById('inputLinea');
    const inputModalidad = document.getElementById('inputModalidad');

    const inputDocente = document.getElementById('inputDocente');
    const inputDocenteCargo = document.getElementById('inputDocenteCargo');
    const inputFotoDocente = document.getElementById('inputFotoDocente');
    const btnQuitarFotoDocente = document.getElementById('btnQuitarFotoDocente');

    const inputDuracion = document.getElementById('inputDuracion');
    const inputCreditos = document.getElementById('inputCreditos');
    const inputInicio = document.getElementById('inputInicio');

    const inputPrecioGeneral = document.getElementById('inputPrecioGeneral');
    const inputPrecioComunidad = document.getElementById('inputPrecioComunidad');

    const inputYapeNumero = document.getElementById('inputYapeNumero');
    const inputYapeTitular = document.getElementById('inputYapeTitular');
    const checkMostrarQr = document.getElementById('checkMostrarQr');

    // Elementos del Lienzo (Flyer Canvas)
    const flyerTitulo = document.getElementById('flyerTitulo');
    const flyerSubtitulo = document.getElementById('flyerSubtitulo');
    const flyerLineaBadge = document.getElementById('flyerLineaBadge');

    const flyerDocente = document.getElementById('flyerDocente');
    const flyerDocenteCargo = document.getElementById('flyerDocenteCargo');
    const flyerDocenteImg = document.getElementById('flyerDocenteImg');
    const flyerDocenteIcon = document.getElementById('flyerDocenteIcon');

    const flyerDuracion = document.getElementById('flyerDuracion');
    const flyerCreditos = document.getElementById('flyerCreditos');
    const flyerInicio = document.getElementById('flyerInicio');
    const flyerModalidad = document.getElementById('flyerModalidad');

    const flyerPrecioGeneral = document.getElementById('flyerPrecioGeneral');
    const flyerPrecioComunidad = document.getElementById('flyerPrecioComunidad');

    const flyerYapeNumero = document.getElementById('flyerYapeNumero');
    const flyerYapeTitular = document.getElementById('flyerYapeTitular');
    const flyerQrWrapper = document.getElementById('flyerQrWrapper');

    // Botones de Acción
    const btnDescargarFlyer = document.getElementById('btnDescargarFlyer');
    const btnCopiarFlyer = document.getElementById('btnCopiarFlyer');

    // Enlace reactivo simple
    function bindInput(inputEl, targetEl, prefix = '', suffix = '') {
        if (!inputEl || !targetEl) return;
        inputEl.addEventListener('input', function () {
            targetEl.textContent = prefix + this.value + suffix;
        });
    }

    bindInput(inputTitulo, flyerTitulo);
    bindInput(inputSubtitulo, flyerSubtitulo);
    bindInput(inputLinea, flyerLineaBadge);
    bindInput(inputModalidad, flyerModalidad);

    bindInput(inputDocente, flyerDocente);
    bindInput(inputDocenteCargo, flyerDocenteCargo);

    bindInput(inputDuracion, flyerDuracion);
    bindInput(inputCreditos, flyerCreditos);
    bindInput(inputInicio, flyerInicio);

    bindInput(inputPrecioGeneral, flyerPrecioGeneral);
    bindInput(inputPrecioComunidad, flyerPrecioComunidad);

    bindInput(inputYapeNumero, flyerYapeNumero);
    bindInput(inputYapeTitular, flyerYapeTitular);

    // Cambio de Tema
    inputTheme.addEventListener('change', function () {
        canvas.classList.remove('theme-granate', 'theme-blue', 'theme-emerald');
        canvas.classList.add(this.value);
    });

    // Cambio de Formato
    inputFormat.addEventListener('change', function () {
        if (this.value === 'format-square') {
            canvas.classList.add('format-square');
        } else {
            canvas.classList.remove('format-square');
        }
    });

    // Toggle QR
    checkMostrarQr.addEventListener('change', function () {
        if (flyerQrWrapper) {
            flyerQrWrapper.style.display = this.checked ? 'block' : 'none';
        }
    });

    // Función para alternar entre Foto Real y Avatar Icono
    function mostrarFotoDocente(src) {
        if (!flyerDocenteImg || !flyerDocenteIcon) return;
        if (src && src.trim() !== '') {
            flyerDocenteImg.src = src;
            flyerDocenteImg.style.display = 'block';
            flyerDocenteIcon.style.display = 'none';
        } else {
            flyerDocenteImg.src = '';
            flyerDocenteImg.style.display = 'none';
            flyerDocenteIcon.style.display = 'flex';
        }
    }

    // Subir foto local desde la computadora
    if (inputFotoDocente) {
        inputFotoDocente.addEventListener('change', function () {
            if (this.files && this.files[0]) {
                const reader = new FileReader();
                reader.onload = function (e) {
                    mostrarFotoDocente(e.target.result);
                };
                reader.readAsDataURL(this.files[0]);
            }
        });
    }

    // Botón para quitar foto y volver a icono
    if (btnQuitarFotoDocente) {
        btnQuitarFotoDocente.addEventListener('click', function () {
            if (inputFotoDocente) inputFotoDocente.value = '';
            mostrarFotoDocente(null);
        });
    }

    // Cargar datos al cambiar de curso
    selectCursoCargar.addEventListener('change', function () {
        const option = this.options[this.selectedIndex];
        if (!option) return;

        const nombre = option.getAttribute('data-nombre') || '';
        const subtitulo = option.getAttribute('data-subtitulo') || '';
        const linea = option.getAttribute('data-linea') || '';
        const docente = option.getAttribute('data-docente') || '';
        const docentecargo = option.getAttribute('data-docentecargo') || 'Investigador Principal IIICCD - FINESI';
        const docentefoto = option.getAttribute('data-docentefoto') || '';
        const duracion = option.getAttribute('data-duracion') || '';
        const creditos = option.getAttribute('data-creditos') || '';
        const precio = option.getAttribute('data-precio') || '';
        const precioComunidad = option.getAttribute('data-preciocomunidad') || '';
        const fechaInicio = option.getAttribute('data-fechainicio') || '';

        // Actualizar inputs
        inputTitulo.value = nombre;
        inputSubtitulo.value = subtitulo;
        inputLinea.value = linea;
        inputDocente.value = docente;
        if (inputDocenteCargo) inputDocenteCargo.value = docentecargo;
        inputDuracion.value = duracion;
        inputCreditos.value = creditos + (creditos.includes('crédito') ? '' : ' créditos universitarios');
        inputInicio.value = fechaInicio;
        inputPrecioGeneral.value = precio.startsWith('S/') ? precio : 'S/ ' + precio;
        inputPrecioComunidad.value = precioComunidad.startsWith('S/') ? precioComunidad : 'S/ ' + precioComunidad;

        // Cargar foto si existe
        if (inputFotoDocente) inputFotoDocente.value = '';
        mostrarFotoDocente(docentefoto);

        // Disparar eventos de actualización al lienzo
        [inputTitulo, inputSubtitulo, inputLinea, inputDocente, inputDocenteCargo, inputDuracion, inputCreditos, inputInicio, inputPrecioGeneral, inputPrecioComunidad].forEach(el => {
            if (el) el.dispatchEvent(new Event('input'));
        });
    });

    // Descarga en Alta Resolución PNG usando html2canvas
    btnDescargarFlyer.addEventListener('click', function () {
        const originalText = btnDescargarFlyer.innerHTML;
        btnDescargarFlyer.disabled = true;
        btnDescargarFlyer.innerHTML = '⏳ Generando imagen en alta resolución...';

        if (typeof html2canvas === 'undefined') {
            alert('Cargando librería de renderizado. Por favor intenta en 3 segundos.');
            btnDescargarFlyer.disabled = false;
            btnDescargarFlyer.innerHTML = originalText;
            return;
        }

        html2canvas(canvas, {
            scale: 2.5, // 2.5x para ultra-alta resolución (nítido en retina y pantallas 4K)
            useCORS: true,
            allowTaint: true,
            backgroundColor: null
        }).then(function (capturedCanvas) {
            const safeName = (inputTitulo.value || 'flyer_iiiccd')
                .toLowerCase()
                .replace(/[^a-z0-9]+/g, '_')
                .substring(0, 35);

            const link = document.createElement('a');
            link.download = safeName + '_flyer.png';
            link.href = capturedCanvas.toDataURL('image/png');
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);

            btnDescargarFlyer.disabled = false;
            btnDescargarFlyer.innerHTML = '✓ ¡Flyer Descargado con Éxito!';
            setTimeout(() => {
                btnDescargarFlyer.innerHTML = originalText;
            }, 3000);
        }).catch(function (err) {
            console.error('Error al capturar canvas:', err);
            alert('No se pudo generar la imagen: ' + err.message);
            btnDescargarFlyer.disabled = false;
            btnDescargarFlyer.innerHTML = originalText;
        });
    });

    // Copiar al Portapapeles
    btnCopiarFlyer.addEventListener('click', function () {
        const originalText = btnCopiarFlyer.innerHTML;
        btnCopiarFlyer.disabled = true;
        btnCopiarFlyer.innerHTML = '⏳ Copiando...';

        if (typeof html2canvas === 'undefined') {
            btnCopiarFlyer.disabled = false;
            btnCopiarFlyer.innerHTML = originalText;
            return;
        }

        html2canvas(canvas, {
            scale: 2,
            useCORS: true,
            allowTaint: true
        }).then(function (capturedCanvas) {
            capturedCanvas.toBlob(function (blob) {
                if (navigator.clipboard && navigator.clipboard.write) {
                    const item = new ClipboardItem({ 'image/png': blob });
                    navigator.clipboard.write([item]).then(function () {
                        btnCopiarFlyer.innerHTML = '✓ ¡Copiado al portapapeles!';
                        setTimeout(() => {
                            btnCopiarFlyer.disabled = false;
                            btnCopiarFlyer.innerHTML = originalText;
                        }, 2500);
                    }).catch(function (err) {
                        alert('Puedes descargar la imagen directamente con el botón verde.');
                        btnCopiarFlyer.disabled = false;
                        btnCopiarFlyer.innerHTML = originalText;
                    });
                } else {
                    alert('Tu navegador no admite pegado directo. Usa el botón "Descargar Flyer".');
                    btnCopiarFlyer.disabled = false;
                    btnCopiarFlyer.innerHTML = originalText;
                }
            });
        });
    });
});
