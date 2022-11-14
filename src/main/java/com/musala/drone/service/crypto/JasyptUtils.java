/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.musala.drone.service.crypto;

import static com.musala.drone.service.utils.LogHandler.logInfo;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

/**
 *
 * @author EBODA
 */
public class JasyptUtils {

    private static String salt;
    private static CryptoAlg alg = CryptoAlg.SYMMENTRIC;

    private static PrivateKey prvkey;
    private static PublicKey pubkey;

    private static final String CRYPTOALGORITHM = "PBEWithMD5AndTripleDES";

    private JasyptUtils() {
    }

    static {
        try {

            prvkey = AsymCrypto.getPrivate("keystore/privateKey");
            pubkey = AsymCrypto.getPublic("keystore/publicKey");
            String cypherText = "EPGTaIRt8hOYzwZto6Blv+1PHXmSkF8IjX495mJV23QxAkSt0qjQ1xPQJZyhSQxMJZ4nTRilpOv/vtu4CFotlXftGXx6QwvzqSZ/sGcUJ0zJ7NTZeVBvzn0Hf3m13pFTaQGja5spHgbGSYZcvomlZzdtNxxYcVfN1i0LmL9Q9A0=";
            salt = decrypt(cypherText, CryptoAlg.ASSYMENTRIC);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static PrivateKey getPrvkey() {
        return prvkey;
    }

    public static void setPrvkey(PrivateKey prvkey) {
        JasyptUtils.prvkey = prvkey;
    }

    public static PublicKey getPubkey() {
        return pubkey;
    }

    public static void setPubkey(PublicKey pubkey) {
        JasyptUtils.pubkey = pubkey;
    }

    public static CryptoAlg getAlg() {
        return alg;

    }

    public static void setAlg(CryptoAlg alg) {
        JasyptUtils.alg = alg;
    }

    public static String getSalt() {
        return salt;
    }

    public static void setSalt(String salt) {
        JasyptUtils.salt = salt;
    }

    public static String encrypt(String plaintText, CryptoAlg alg) throws Exception {

        if (null == alg) {
            throw new RuntimeException("CryptoAlg null cannot be use to encrypt, set crypto Algorithm [setAlg]");
        } else {
            switch (alg) {
                case SYMMENTRIC:
                    return encrypt(plaintText);
                case ASSYMENTRIC:
                    return assymentricEncrypt(plaintText);

                default:
                    throw new RuntimeException("Unknown Crypto Type " + alg + ", set crypto Algorithm [setAlg]");
            }
        }
    }

    public static String assymentricEncrypt(String plaintText) throws Exception {
        Cipher cipher = AsymCrypto.getAsymmetricCipher();
        cipher.init(Cipher.ENCRYPT_MODE, pubkey);
        byte[] encryptedMessage = cipher.doFinal(plaintText.getBytes(StandardCharsets.UTF_8));
        return new String(java.util.Base64.getEncoder().encode(encryptedMessage));
    }

    public static String decrypt(String cypherText, CryptoAlg alg) throws Exception {
        if (null == alg) {
            throw new RuntimeException("CryptoAlg null cannot be use to decrypt, set crypto Algorithm [setAlg]");
        } else {
            switch (alg) {
                case SYMMENTRIC:
                    return decrypt(cypherText);
                case ASSYMENTRIC:
                    return assymentricDecrypt(cypherText);

                default:
                    throw new RuntimeException("Unknown Crypto Type " + alg + ", set crypto Algorithm [setAlg]");
            }
        }

    }

    public static String assymentricDecrypt(String cypherText) throws Exception {
        Cipher cipher = AsymCrypto.getAsymmetricCipher();
        cipher.init(Cipher.DECRYPT_MODE, prvkey);
        byte[] cipherbits = java.util.Base64.getDecoder().decode(cypherText);
        byte[] plainMessage = cipher.doFinal(cipherbits);
        return new String(plainMessage, StandardCharsets.UTF_8);
    }

    public static String encrypt(String plaintText) {

        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(salt);
        encryptor.setAlgorithm(CRYPTOALGORITHM);
        return encryptor.encrypt(plaintText);
    }

    public static String encryptAndSalt(String plaintText) {

        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(salt);
        encryptor.setAlgorithm(CRYPTOALGORITHM);
        return encryptor.encrypt(plaintText) + "\nSalt=" + encryptor.encrypt(salt);
    }

    public static String decrypt(String cypherText) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(salt);
        encryptor.setAlgorithm(CRYPTOALGORITHM);
        return encryptor.decrypt(cypherText);

    }

    public enum CryptoAlg {
        SYMMENTRIC,
        ASSYMENTRIC
    }

    public static void main(String... args) {
        String cypherText = "UMh74mmFiT/+Hx9z8aNMRRhZNiev3tsM";
        String plain = decrypt(cypherText);
        logInfo(plain);
        plain = "networking";
        cypherText = encrypt(plain);
        logInfo(cypherText);
    }

    private static class AsymCrypto {

        private AsymCrypto() {
        }

        public static Cipher getAsymmetricCipher() throws NoSuchAlgorithmException, NoSuchPaddingException {
            return Cipher.getInstance("RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING");
        }

        public static PrivateKey getPrivate(String filename) throws Exception {
            byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(spec);
        }

        public static PublicKey getPublic(String filename) throws Exception {
            byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(spec);
        }
    }
}
