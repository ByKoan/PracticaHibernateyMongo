package org.example.hibernate.mongofiles;

import java.security.MessageDigest;

public class HashUtil {

    // Clase que aplica el algoritmo de hash SHA-256

    public static String sha256(String input) {
        try {
            // Declara el algoritmo SHA-256.
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Convierte la cadena de entrada a bytes usando codificación UTF-8
            // y calcula el hash (resumen) usando SHA-256.
            byte[] hash = digest.digest(input.getBytes("UTF-8"));

            // StringBuilder para ir construyendo el resultado hexadecimal del hash.
            StringBuilder hexString = new StringBuilder();

            // Recorre cada byte del hash generado.
            for (byte b : hash) {
                // Convierte cada byte a su representación hexadecimal.
                String hex = Integer.toHexString(0xff & b);

                // Si el valor hexadecimal tiene un solo dígito, añade un 0 delante
                // para mantener siempre dos dígitos por byte.
                if (hex.length() == 1) hexString.append('0');

                // Añade el valor hexadecimal al resultado final.
                hexString.append(hex);
            }

            // Devuelve el hash completo en formato hexadecimal (64 caracteres).
            return hexString.toString();

        } catch (Exception e) {
            // Si ocurre algún error (por ejemplo, el algoritmo no existe),
            throw new RuntimeException("Error generando hash SHA-256", e);
        }
    }
}