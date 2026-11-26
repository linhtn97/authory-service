package com.linhtn.authory_service.config.security.provider;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.linhtn.authory_service.config.security.config.SecurityProperties;

@Component
public class RsaKeyManager {
    private static final Logger log = LoggerFactory.getLogger(RsaKeyManager.class);
    public static final String KEY_ID = "authory-key-1";

    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;
    private final boolean rsaEnabled;

    public RsaKeyManager(SecurityProperties securityProperties) {
        SecurityProperties.JwtConfig jwtConfig =
                securityProperties != null ? securityProperties.getJwt() : null;

        if (jwtConfig != null && "RS256".equalsIgnoreCase(jwtConfig.getAlgorithm())) {
            this.rsaEnabled = true;
            KeyPair pair = initializeKeyPair(jwtConfig);
            this.privateKey = (RSAPrivateKey) pair.getPrivate();
            this.publicKey = (RSAPublicKey) pair.getPublic();
            log.info("Initialized RSA Key Pair for RS256 token signing (kid: {})", KEY_ID);
        } else {
            this.rsaEnabled = false;
            this.privateKey = null;
            this.publicKey = null;
        }
    }

    private KeyPair initializeKeyPair(SecurityProperties.JwtConfig jwtConfig) {
        try {
            if (jwtConfig.getPrivateKey() != null
                    && !jwtConfig.getPrivateKey().isBlank()
                    && jwtConfig.getPublicKey() != null
                    && !jwtConfig.getPublicKey().isBlank()) {
                KeyFactory kf = KeyFactory.getInstance("RSA");

                String privPem = cleanPem(jwtConfig.getPrivateKey());
                byte[] privBytes = Base64.getDecoder().decode(privPem);
                RSAPrivateKey privKey =
                        (RSAPrivateKey) kf.generatePrivate(new PKCS8EncodedKeySpec(privBytes));

                String pubPem = cleanPem(jwtConfig.getPublicKey());
                byte[] pubBytes = Base64.getDecoder().decode(pubPem);
                RSAPublicKey pubKey =
                        (RSAPublicKey) kf.generatePublic(new X509EncodedKeySpec(pubBytes));

                return new KeyPair(pubKey, privKey);
            }

            // Auto-generate 2048-bit RSA key pair if not explicitly provided
            log.warn(
                    "No explicit RSA keys configured; auto-generating a 2048-bit RSA key pair for RS256 signing.");
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            return generator.generateKeyPair();
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to initialize RSA key pair for RS256 signing", e);
        }
    }

    private String cleanPem(String key) {
        return key.replaceAll("-----[A-Z ]+-----", "").replaceAll("\\s+", "");
    }

    public boolean isRsaEnabled() {
        return rsaEnabled;
    }

    public RSAPrivateKey getPrivateKey() {
        return privateKey;
    }

    public RSAPublicKey getPublicKey() {
        return publicKey;
    }

    public Map<String, Object> getJwks() {
        if (!rsaEnabled || publicKey == null) {
            return Map.of("keys", List.of());
        }

        String n =
                Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString(toUnsignedByteArray(publicKey.getModulus()));
        String e =
                Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString(toUnsignedByteArray(publicKey.getPublicExponent()));

        Map<String, Object> jwk =
                Map.of("kty", "RSA", "use", "sig", "alg", "RS256", "kid", KEY_ID, "n", n, "e", e);

        return Map.of("keys", List.of(jwk));
    }

    private static byte[] toUnsignedByteArray(BigInteger bigInt) {
        byte[] array = bigInt.toByteArray();
        if (array.length > 1 && array[0] == 0) {
            byte[] copy = new byte[array.length - 1];
            System.arraycopy(array, 1, copy, 0, copy.length);
            return copy;
        }
        return array;
    }
}
