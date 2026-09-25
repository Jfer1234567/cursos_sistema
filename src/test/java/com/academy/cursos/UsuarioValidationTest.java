package com.academy.cursos;

import com.academy.cursos.dto.RegistroDTO;
import com.academy.cursos.model.Usuario;
import com.academy.cursos.model.enums.TipoDocumento;
import com.academy.cursos.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UsuarioValidationTest {

    @Autowired
    private UsuarioService usuarioService;

    @Test
    void testRechazarDniInvalido() {
        RegistroDTO dto = new RegistroDTO();
        dto.setNombreCompleto("Usuario Test Invalido");
        dto.setTipoDocumento(TipoDocumento.DNI);
        dto.setNumeroDocumento("12345"); // Incompleto: 5 dígitos
        dto.setCorreo("testinvalido1@test.com");
        dto.setTelefono("951234567");
        dto.setInstitucionProcedencia("UNA Puno");
        dto.setPassword("123456");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.registrar(dto);
        });

        assertTrue(ex.getMessage().contains("8 dígitos"), "Debe rechazar DNI que no tenga 8 dígitos");
    }

    @Test
    void testRechazarTelefonoInvalido() {
        RegistroDTO dto = new RegistroDTO();
        dto.setNombreCompleto("Usuario Test Invalido");
        dto.setTipoDocumento(TipoDocumento.DNI);
        dto.setNumeroDocumento("88776655"); // 8 dígitos válido
        dto.setCorreo("testinvalido2@test.com");
        dto.setTelefono("9512345"); // Incompleto: 7 dígitos
        dto.setInstitucionProcedencia("UNA Puno");
        dto.setPassword("123456");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.registrar(dto);
        });

        assertTrue(ex.getMessage().contains("9 dígitos"), "Debe rechazar teléfono que no tenga 9 dígitos");
    }

    @Test
    void testAceptarDni8DigitosYTelefono9Digitos() {
        String numDocUnico = "8" + (System.currentTimeMillis() % 10000000);
        if (numDocUnico.length() < 8) {
            numDocUnico = String.format("%08d", Long.parseLong(numDocUnico));
        } else if (numDocUnico.length() > 8) {
            numDocUnico = numDocUnico.substring(0, 8);
        }

        RegistroDTO dto = new RegistroDTO();
        dto.setNombreCompleto("Estudiante Valido");
        dto.setTipoDocumento(TipoDocumento.DNI);
        dto.setNumeroDocumento(numDocUnico);
        dto.setCorreo("valido_" + System.currentTimeMillis() + "@test.com");
        dto.setTelefono("987654321"); // 9 dígitos
        dto.setInstitucionProcedencia("UNA Puno");
        dto.setPassword("123456");

        Usuario u = usuarioService.registrar(dto);
        assertNotNull(u.getId());
        assertEquals(8, u.getNumeroDocumento().length());
        assertEquals(9, u.getTelefono().length());
    }
}
