package com.example.cloudstore;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
public class SecurityUtil {
/////////////////////////////////////////////////////////////////////////////////////
//    private final static int READ_WRITE_BLOCK_BUFFER = 1024;
//    private final static String SECRET_KEY = "AES";
//    private final static String FILE_ENCRYPTOR = "AES/CBC/PKCS5Padding";
//
//

//    public static void encryptFile (String keyStr, String specStr, InputStream inputStream, OutputStream outputStream)
//            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IOException {
//        try {
//            IvParameterSpec ivParameterSpec = new IvParameterSpec(specStr.getBytes("UTF-8"));
//
//            SecretKeySpec keySpec = new SecretKeySpec(keyStr.getBytes("UTF-8"), SECRET_KEY);
//
//            Cipher cipher = Cipher.getInstance(FILE_ENCRYPTOR);
//            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParameterSpec);
//            outputStream = new CipherOutputStream(outputStream, cipher);
//            int count = 0;
//            byte[] buffer = new byte[READ_WRITE_BLOCK_BUFFER];
//            while ((count = inputStream.read(buffer)) > 0) {
//                outputStream.write(buffer, 0, count);
//            }
//         } finally{
//            outputStream.close();
//        }
//
//    }
//
//    public static void decryptFile (String keyStr, String specStr, InputStream inputStream, OutputStream outputStream)
//            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IOException {
//        try {
//            IvParameterSpec ivParameterSpec = new IvParameterSpec(specStr.getBytes("UTF-8"));
//
//            SecretKeySpec keySpec = new SecretKeySpec(keyStr.getBytes("UTF-8"), SECRET_KEY);
//
//            Cipher cipher = Cipher.getInstance(FILE_ENCRYPTOR);
//            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec);
//            outputStream = new CipherOutputStream(outputStream, cipher);
//            int count = 0;
//            byte[] buffer = new byte[READ_WRITE_BLOCK_BUFFER];
//            while ((count = inputStream.read(buffer)) > 0) {
//                outputStream.write(buffer, 0, count);
//            }
//        } finally{
//            outputStream.close();
//        }
//
//    }

/////////////////////////////////////////////////////////////////////////////////


    private static String SECRET_KEY = "AES";
    private static String passwordString;

    public static SecretKey generateKey() throws NoSuchAlgorithmException {
        // Generate a 256-bit key
        final int outputKeyLength = 256;
        SecureRandom secureRandom = new SecureRandom();
        byte salt[] = new byte[256];
        secureRandom.nextBytes(salt);
        char[] passwordChar = passwordString.toCharArray(); //Turn password into char[] array
        PBEKeySpec pbKeySpec = new PBEKeySpec(passwordChar, salt, 1324, 256); //1324 iterations
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] keyBytes = new byte[0];
        try {
            keyBytes = secretKeyFactory.generateSecret(pbKeySpec).getEncoded();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

        return keySpec;


/*        // Do *not* seed secureRandom! Automatically seeded from system entropy.
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(outputKeyLength, secureRandom);
        SecretKey yourKey = keyGenerator.generateKey();
        return yourKey;*/
    }

    public static void ivGenerate(){

        //Generating initialization vector (IV)
        SecureRandom ivRandom = new SecureRandom(); //not caching previous seeded instance of SecureRandom
        byte[] iv = new byte[16];
        ivRandom.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

    }

    private HashMap<String, byte[]> encryptBytes(byte[] plainTextBytes, String passwordString)
    {
        HashMap<String, byte[]> map = new HashMap<String, byte[]>();

        try
        {
            //Random salt for next step
            SecureRandom random = new SecureRandom();
            byte salt[] = new byte[256];
            random.nextBytes(salt);

            //PBKDF2 - derive the key from the password, don't use passwords directly
            char[] passwordChar = passwordString.toCharArray(); //Turn password into char[] array
            PBEKeySpec pbKeySpec = new PBEKeySpec(passwordChar, salt, 1324, 256); //1324 iterations
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] keyBytes = secretKeyFactory.generateSecret(pbKeySpec).getEncoded();
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

            //Create initialization vector for AES
            SecureRandom ivRandom = new SecureRandom(); //not caching previous seeded instance of SecureRandom
            byte[] iv = new byte[16];
            ivRandom.nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            //Encrypt
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] encrypted = cipher.doFinal(plainTextBytes);

            map.put("salt", salt);
            map.put("iv", iv);
            map.put("encrypted", encrypted);
        }
        catch(Exception e)
        {
            Log.e("MYAPP", "encryption exception", e);
        }

        return map;
    }

    private byte[] decryptData(HashMap<String, byte[]> map, String passwordString)
    {
        byte[] decrypted = null;
        try
        {
            byte salt[] = map.get("salt");
            byte iv[] = map.get("iv");
            byte encrypted[] = map.get("encrypted");

            //regenerate key from password
            char[] passwordChar = passwordString.toCharArray();
            PBEKeySpec pbKeySpec = new PBEKeySpec(passwordChar, salt, 1324, 256);
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] keyBytes = secretKeyFactory.generateSecret(pbKeySpec).getEncoded();
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

            //Decrypt
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            decrypted = cipher.doFinal(encrypted);
        }
        catch(Exception e)
        {
            Log.e("MYAPP", "decryption exception", e);
        }

        return decrypted;
    }

/*    public static byte[] encodeFile(SecretKey yourKey, byte[] fileData)
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
    }*/
/*    public static byte[] decodeFile(SecretKey yourKey, byte[] fileData)
            throws Exception {
        byte[] decrypted = null;
        Cipher cipher = Cipher.getInstance(SECRET_KEY);
        cipher.init(Cipher.DECRYPT_MODE, yourKey, new IvParameterSpec(
                new byte[cipher.getBlockSize()]));
        decrypted = cipher.doFinal(fileData);
        return decrypted;
    }*/

}
