package uk.co.perspective.app.security;

import android.util.Base64;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Locale;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Rijndael {
    public static String encrypt(String plainText, byte[] key) {
        try
        {
            Cipher cipher = Cipher.getInstance("AES/NoPadding");
            int blockSize = cipher.getBlockSize();
            byte[] dataBytes = plainText.getBytes();
            int plaintTextLength = dataBytes.length;
            if (plaintTextLength % blockSize != 0)
            {
                plaintTextLength = plaintTextLength + (blockSize - (plaintTextLength % blockSize));
            }
            byte[] plainTextByte = new byte[plaintTextLength];
            System.arraycopy(dataBytes, 0, plainTextByte, 0, dataBytes.length);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"));
            return Arrays.toString(Base64.encode(cipher.doFinal(plainTextByte), Base64.DEFAULT)) + String.format(Locale.UK, "%06d", plainText.length());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e)
        {
            e.printStackTrace();
        }
        return "";
    }

    public static  String decrypt(String cipherText, byte[] key) {
        int plainTextLength = Integer.parseInt(cipherText.substring(cipherText.length() - 6));
        cipherText = cipherText.substring(0, cipherText.length() - 6);
        try
        {
            Cipher cipher = Cipher.getInstance("AES/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"));
            return new String(cipher.doFinal(Base64.decode(cipherText.getBytes(), Base64.DEFAULT))).substring(0, plainTextLength);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e)
        {
            e.printStackTrace();
        }
        return "";
    }
}