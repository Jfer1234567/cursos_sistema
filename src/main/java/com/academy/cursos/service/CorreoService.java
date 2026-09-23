package com.academy.cursos.service;

import com.academy.cursos.model.Curso;
import com.academy.cursos.model.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class CorreoService {

    private static final Logger log = LoggerFactory.getLogger(CorreoService.class);

    private final JavaMailSender mailSender;

    public CorreoService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    private void enviarCorreoSeguro(String para, String asunto, String contenido) {
        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setTo(para);
            mensaje.setSubject(asunto);
            mensaje.setText(contenido);
            mensaje.setFrom("iiiccd.finesi@gmail.com");
            mailSender.send(mensaje);
            log.info("Correo enviado a {} con asunto: {}", para, asunto);
        } catch (Exception e) {
            log.warn("No se pudo enviar el correo a {}: {}", para, e.getMessage());
        }
    }

    public void enviarBienvenida(Usuario u) {
        String asunto = "Bienvenido al IIICCD - UNA Puno";
        String contenido = "Estimado(a) " + u.getNombreCompleto() + ",\n\n" +
                "Te damos la bienvenida al Instituto de Investigación en Inteligencia Computacional y Ciencia de Datos (IIICCD) de la FINESI - UNA Puno.\n\n" +
                "Tu cuenta ha sido creada exitosamente con el correo: " + u.getCorreo() + ".\n\n" +
                "Ya puedes acceder a nuestra plataforma e inscribirte en nuestros cursos y especializaciones.\n\n" +
                "Atentamente,\nDirectiva IIICCD";
        enviarCorreoSeguro(u.getCorreo(), asunto, contenido);
    }

    public void enviarInscripcionCreada(Usuario u, Curso c) {
        String asunto = "Inscripción recibida: " + c.getNombre();
        String contenido = "Estimado(a) " + u.getNombreCompleto() + ",\n\n" +
                "Hemos recibido tu registro de inscripción y comprobante de pago para el curso: " + c.getNombre() + ".\n\n" +
                "Tu inscripción se encuentra actualmente en estado: PENDIENTE DE VERIFICACIÓN.\n" +
                "El equipo de administración revisará el comprobante adjunto y recibirás una notificación cuando sea aprobado.\n\n" +
                "Atentamente,\nDirectiva IIICCD";
        enviarCorreoSeguro(u.getCorreo(), asunto, contenido);
    }

    public void enviarPagoAprobado(Usuario u, Curso c) {
        String asunto = "¡Pago aprobado! — Acceso a " + c.getNombre();
        String enlace = (c.getEnlaceClase() != null && !c.getEnlaceClase().isBlank())
                ? c.getEnlaceClase()
                : "Se publicará en tu panel de participante antes de iniciar las clases.";

        String contenido = "Estimado(a) " + u.getNombreCompleto() + ",\n\n" +
                "Tu pago para el curso \"" + c.getNombre() + "\" ha sido VERIFICADO Y APROBADO satisfactoriamente.\n\n" +
                "Enlace de acceso a las sesiones virtuales (Zoom/Meet):\n" + enlace + "\n\n" +
                "Puedes revisar todos los detalles ingresando a tu panel de participante.\n\n" +
                "¡Muchos éxitos en tu capacitación!\n" +
                "Atentamente,\nDirectiva IIICCD";
        enviarCorreoSeguro(u.getCorreo(), asunto, contenido);
    }

    public void enviarPagoRechazado(Usuario u, Curso c, String motivo) {
        String asunto = "Observación en tu comprobante de pago — " + c.getNombre();
        String contenido = "Estimado(a) " + u.getNombreCompleto() + ",\n\n" +
                "Tu comprobante de pago para el curso \"" + c.getNombre() + "\" no ha podido ser validado.\n\n" +
                "Motivo u observación:\n" + (motivo != null ? motivo : "Comprobante ilegible o datos no coincidentes.") + "\n\n" +
                "Por favor, ingresa a tu panel de participante para volver a subir un comprobante válido.\n\n" +
                "Atentamente,\nDirectiva IIICCD";
        enviarCorreoSeguro(u.getCorreo(), asunto, contenido);
    }

    public void enviarCertificadoEmitido(Usuario u, Curso c, String codigoVerificacion) {
        String asunto = "Tu certificado está listo para descargar — " + c.getNombre();
        String contenido = "Estimado(a) " + u.getNombreCompleto() + ",\n\n" +
                "¡Felicitaciones! Has completado satisfactoriamente el curso \"" + c.getNombre() + "\".\n\n" +
                "Tu certificado de participación oficial ha sido emitido con el código único: " + codigoVerificacion + ".\n\n" +
                "Puedes descargarlo directamente en formato PDF desde tu panel de participante.\n" +
                "Cualquier entidad puede validar su autenticidad ingresando dicho código en la sección de validación pública de nuestra plataforma.\n\n" +
                "Atentamente,\nDirectiva IIICCD";
        enviarCorreoSeguro(u.getCorreo(), asunto, contenido);
    }
}
