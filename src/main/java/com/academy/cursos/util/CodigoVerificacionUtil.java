package com.academy.cursos.util;

import java.security.SecureRandom;

public class CodigoVerificacionUtil {

    private static final String CARACTERES = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int LONGITUD = 6;
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generarCodigo() {
        StringBuilder sb = new StringBuilder("IIICCD-");
        for (int i = 0; i < LONGITUD; i++) {
            int index = RANDOM.nextInt(CARACTERES.length());
            sb.append(CARACTERES.charAt(index));
        }
        return sb.toString();
    }
}
