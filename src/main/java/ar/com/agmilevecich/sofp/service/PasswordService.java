package ar.com.agmilevecich.sofp.service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;

public final class PasswordService {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 210_000;
    private static final int SALT_LENGTH = 16;
    private static final int KEY_LENGTH = 256;

    private PasswordService() {
    }

    public static String hash(String password) {

        Objects.requireNonNull(
                password,
                "La contraseña es obligatoria"
        );

        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);

        byte[] derivedKey = derive(password, salt, ITERATIONS);

        return "pbkdf2$"
                + ITERATIONS
                + "$"
                + Base64.getEncoder().encodeToString(salt)
                + "$"
                + Base64.getEncoder().encodeToString(derivedKey);
    }

    public static boolean matches(String password, String passwordHash) {

        Objects.requireNonNull(
                password,
                "La contraseña es obligatoria"
        );
        Objects.requireNonNull(
                passwordHash,
                "El password hash es obligatorio"
        );

        try {
            String[] parts = passwordHash.split("\\$", -1);

            if (parts.length != 4 || !"pbkdf2".equals(parts[0])) {
                return false;
            }

            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expected = Base64.getDecoder().decode(parts[3]);
            byte[] actual = derive(password, salt, iterations);

            return java.security.MessageDigest.isEqual(actual, expected);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] derive(
            String password,
            byte[] salt,
            int iterations) {

        try {
            PBEKeySpec specification =
                    new PBEKeySpec(
                            password.toCharArray(),
                            salt,
                            iterations,
                            KEY_LENGTH
                    );

            try {
                return SecretKeyFactory
                        .getInstance(ALGORITHM)
                        .generateSecret(specification)
                        .getEncoded();
            } finally {
                specification.clearPassword();
            }
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(
                    "No se pudo procesar la contraseña",
                    e
            );
        }
    }
}
