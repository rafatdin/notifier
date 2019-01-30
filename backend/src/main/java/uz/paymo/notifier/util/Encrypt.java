package uz.paymo.notifier.util;

import org.springframework.data.util.Pair;
import uz.paymo.notifier.domain.PartnerSystem;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by rafatdin on 1/30/19.
 */
public class Encrypt {
    private static Integer ITERATION_COUNT = 40000;
    private static Integer KEY_LENGTH = 128;

/*    public static void main(String[] args) throws Exception {
        String password =  "GRyH3*8c0Gdqjf-Mqc"; *//*System.getProperty("password");
        if (password == null) {
            throw new IllegalArgumentException("Run with -Dpassword=<password>");
        }*//*

        // The salt (probably) can be stored along with the encrypted data
        byte[] salt = new String("6aS@9GceHv@aohXdtPn@mtwMFvBkGOxf").getBytes();

        // Decreasing this speeds down startup time and can be useful during testing, but it also makes it easier for brute force attackers
        int iterationCount = 40000;
        // Other values give me java.security.InvalidKeyException: Illegal key size or default parameters
        int keyLength = 128;
        SecretKeySpec key = createSecretKey(password.toCharArray(),
                salt, iterationCount, keyLength);

        String originalPassword = "6aS@9GceHv@aohXdtPn@mtwMFvBkGOxf";
        System.out.println("Original password: " + originalPassword);
        String encryptedPassword = encrypt(originalPassword, key);
        System.out.println("Encrypted password: " + encryptedPassword);
        String decryptedPassword = decrypt(encryptedPassword, key);
        System.out.println("Decrypted password: " + decryptedPassword);
    }*/

    public static Pair<String,byte[]> encrypt(String partnerPassword) throws GeneralSecurityException, UnsupportedEncodingException {
        String password = System.getProperty("password");
        if (password == null) {
            throw new IllegalArgumentException("Run with -Dpassword=<password>");
        }
        byte [] salt = generateSalt();
        SecretKeySpec key = createSecretKey(password.toCharArray(),
                salt, ITERATION_COUNT, KEY_LENGTH);

        return Pair.of(encrypt(partnerPassword, key), salt);
    }

    public static String decrypt(PartnerSystem partner) throws GeneralSecurityException, IOException {
        String password = System.getProperty("password");
        if (password == null) {
            throw new IllegalArgumentException("Run with -Dpassword=<password>");
        }
        SecretKeySpec key = createSecretKey(password.toCharArray(),
                partner.getSalt(), ITERATION_COUNT, KEY_LENGTH);
        return decrypt(partner.getPassword(), key);
    }


    private static SecretKeySpec createSecretKey(char[] password, byte[] salt, int iterationCount, int keyLength) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        PBEKeySpec keySpec = new PBEKeySpec(password, salt, iterationCount, keyLength);
        SecretKey keyTmp = keyFactory.generateSecret(keySpec);
        return new SecretKeySpec(keyTmp.getEncoded(), "AES");
    }

    private static String encrypt(String property, SecretKeySpec key) throws GeneralSecurityException, UnsupportedEncodingException {
        Cipher pbeCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        pbeCipher.init(Cipher.ENCRYPT_MODE, key);
        AlgorithmParameters parameters = pbeCipher.getParameters();
        IvParameterSpec ivParameterSpec = parameters.getParameterSpec(IvParameterSpec.class);
        byte[] cryptoText = pbeCipher.doFinal(property.getBytes("UTF-8"));
        byte[] iv = ivParameterSpec.getIV();
        return base64Encode(iv) + ":" + base64Encode(cryptoText);
    }

    private static String base64Encode(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    private static String decrypt(String string, SecretKeySpec key) throws GeneralSecurityException, IOException {
        String iv = string.split(":")[0];
        String property = string.split(":")[1];
        Cipher pbeCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        pbeCipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(base64Decode(iv)));
        return new String(pbeCipher.doFinal(base64Decode(property)), "UTF-8");
    }

    private static byte[] base64Decode(String property) throws IOException {
        return Base64.getDecoder().decode(property);
    }

    private static byte[] generateSalt(){
        Random r = new SecureRandom();
        byte[] salt = new byte[20];
        r.nextBytes(salt);
        return salt;
    }

    public static void main(String[] args) {
        byte [] salt = generateSalt();
        System.out.println(new String(salt, Charset.forName("UTF-8")));
    }
}