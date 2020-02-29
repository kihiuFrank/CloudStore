package com.example.cloudstore;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class SecurityUtil {
/////////////////////////////////////////////////////////////////////////////////////
    private final static int READ_WRITE_BLOCK_BUFFER = 1024;
    private final static String SECRET_KEY = "AES";
    private final static String FILE_ENCRYPTOR = "AES/CBC/PKCS5Padding";

    public static void encryptFile (String keyStr, String specStr, InputStream inputStream, OutputStream outputStream)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IOException {
        try {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(specStr.getBytes("UTF-8"));

            SecretKeySpec keySpec = new SecretKeySpec(keyStr.getBytes("UTF-8"), SECRET_KEY);

            Cipher cipher = Cipher.getInstance(FILE_ENCRYPTOR);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParameterSpec);
            outputStream = new CipherOutputStream(outputStream, cipher);
            int count = 0;
            byte[] buffer = new byte[READ_WRITE_BLOCK_BUFFER];
            while ((count = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, count);
            }
         } finally{
            outputStream.close();
        }

    }

    public static void decryptFile (String keyStr, String specStr, InputStream inputStream, OutputStream outputStream)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IOException {
        try {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(specStr.getBytes("UTF-8"));

            SecretKeySpec keySpec = new SecretKeySpec(keyStr.getBytes("UTF-8"), SECRET_KEY);

            Cipher cipher = Cipher.getInstance(FILE_ENCRYPTOR);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec);
            outputStream = new CipherOutputStream(outputStream, cipher);
            int count = 0;
            byte[] buffer = new byte[READ_WRITE_BLOCK_BUFFER];
            while ((count = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, count);
            }
        } finally{
            outputStream.close();
        }

    }

/////////////////////////////////////////////////////////////////////////////////


/*    private static String SECRET_KEY = "AES";
    public static SecretKey generateKey() throws NoSuchAlgorithmException {
        // Generate a 256-bit key
        final int outputKeyLength = 256;
        SecureRandom secureRandom = new SecureRandom();
        // Do *not* seed secureRandom! Automatically seeded from system entropy.
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(outputKeyLength, secureRandom);
        SecretKey yourKey = keyGenerator.generateKey();
        return yourKey;
    }
    public static byte[] encodeFile(SecretKey yourKey, byte[] fileData)
            throws Exception {
        byte[] encrypted = null;
        byte[] data = yourKey.getEncoded();
        SecretKeySpec skeySpec = new SecretKeySpec(data, 0, data.length,
                SECRET_KEY);
        Cipher cipher = Cipher.getInstance(SECRET_KEY);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(
                new byte[cipher.getBlockSize()]));
        encrypted = cipher.doFinal(fileData);
        return encrypted;
    }
    public static byte[] decodeFile(SecretKey yourKey, byte[] fileData)
            throws Exception {
        byte[] decrypted = null;
        Cipher cipher = Cipher.getInstance(SECRET_KEY);
        cipher.init(Cipher.DECRYPT_MODE, yourKey, new IvParameterSpec(
                new byte[cipher.getBlockSize()]));
        decrypted = cipher.doFinal(fileData);
        return decrypted;
    }*/

}
