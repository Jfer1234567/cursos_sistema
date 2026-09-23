// admin.js - Interacciones en el panel de administración
document.addEventListener('DOMContentLoaded', () => {
    // Confirmación al rechazar un pago
    const rejectForms = document.querySelectorAll('.form-rechazar-pago');
    rejectForms.forEach(form => {
        form.addEventListener('submit', (e) => {
            const motivo = form.querySelector('input[name="motivo"]').value;
            if (!motivo || motivo.trim() === '') {
                e.preventDefault();
                alert('Debe especificar un motivo para el rechazo del pago.');
            }
        });
    });
});
