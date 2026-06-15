package com.app.and.integracao;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Criptografia simétrica (AES-256-GCM) para guardar segredos das integrações.
 * A senha e o salt vêm de variáveis de ambiente em produção
 * (APP_CRYPTO_PASSWORD / APP_CRYPTO_SALT).
 *
 * O salt pode ser qualquer texto: se não for um hexadecimal válido, é
 * convertido de forma determinística (SHA-256) para um salt hexadecimal,
 * evitando erros de configuração.
 */
@Service
public class CryptoService {

    private final TextEncryptor encryptor;

    public CryptoService(
            @Value("${app.crypto.password}") String password,
            @Value("${app.crypto.salt}") String salt) {
        this.encryptor = Encryptors.delux(password, normalizarSaltHex(salt));
    }

    public String encrypt(String texto) {
        return texto == null ? null : encryptor.encrypt(texto);
    }

    public String decrypt(String cifrado) {
        return cifrado == null ? null : encryptor.decrypt(cifrado);
    }

    /** Garante um salt em hexadecimal (8 bytes / 16 chars), aceitando qualquer entrada. */
    private static String normalizarSaltHex(String salt) {
        if (salt != null && salt.matches("(?i)[0-9a-f]+") && salt.length() % 2 == 0) {
            return salt;
        }
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(String.valueOf(salt).getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                hex.append(String.format("%02x", hash[i]));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 indisponível", e);
        }
    }
}
