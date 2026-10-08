package util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.text.Normalizer;
import java.util.Base64;

public class Seguridad {

    private static final int iteraciones = 50000;
    private static final int longitudClave = 256;

    // Generar un salt aleatorio.
    public static String generarSalt() {
        byte[] salt = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    // Generar un Hash seguro para la contraseña.
    public static String generarHash(String clave, String saltTexto) {
        try {
            byte[] salt = Base64.getDecoder().decode(saltTexto);
            PBEKeySpec spec = new PBEKeySpec(clave.toCharArray(), salt, iteraciones, longitudClave);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error al generar hash de contraseña.", e);
        }
    }

    // Verificar si la contraseña ingresada coincide con la almacenada.
    public static boolean verificarClave(String claveIngresada, String salt, String hashGuardado) {
        if (claveIngresada == null || salt == null || hashGuardado == null) {
            return false;
        }
        String hashIngresado = generarHash(claveIngresada, salt);
        return hashIngresado.equals(hashGuardado);
    }

    // Validar que la contraseña cumpla los requisitos mínimos.
    public static boolean validarFormatoClave(String clave) {
        if (clave == null) {
            return false;
        }
        return clave.length() >= 8
                && clave.matches(".*[A-Z].*")
                && clave.matches(".*[a-z].*")
                && clave.matches(".*\\d.*")
                && clave.matches(".*[^a-zA-Z0-9].*");
    }

    // Generar el nombre de usuario como NombreApellido.
    public static String generarUsuario(String nombre, String apellido) {
        String usuario = nombre.trim() + apellido.trim();
        usuario = Normalizer.normalize(usuario, Normalizer.Form.NFD);
        usuario = usuario.replaceAll("\\p{M}", "");
        usuario = usuario.replaceAll("[^a-zA-Z0-9]", "");
        return usuario;
    }

}