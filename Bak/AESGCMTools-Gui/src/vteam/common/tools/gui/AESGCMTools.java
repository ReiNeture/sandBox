package vteam.common.tools.gui;

import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESGCMTools {
	
    private static final int AES_KEY_SIZE = 256; // 可選 128、192、256
    private static final int GCM_IV_LENGTH = 12; // GCM 建議 12 bytes IV
    private static final int GCM_TAG_LENGTH = 16; // 128-bit 認證標籤 (16 bytes)
    private static final String K = "XogJRRg0kveiSsU95OP1qXQHSfX8j+PxDvT6GK7RF5E=";
    
    /**
     * 使用環境 key 解密(Data Base64)，設定檔使用
     */
    public static String decrypt(String encryptedData) throws Exception {
    	byte[] retrievedData = Base64.getDecoder().decode(encryptedData);
    	return decrypt(retrievedData, getSecretKeyFromEnv());
    }
    
    /**
     * AES-GCM 解密主體，private
     */
    private static String decrypt(byte[] encryptedData, SecretKey key) throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.wrap(encryptedData);

        // 讀取 IV
        byte[] iv = new byte[GCM_IV_LENGTH];
        byteBuffer.get(iv);

        // 讀取密文
        byte[] cipherText = new byte[byteBuffer.remaining()];
        byteBuffer.get(cipherText);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, gcmParameterSpec);

        // 附加驗證資料（需與加密時相同）
        cipher.updateAAD("Chocolat".getBytes());

        // 執行解密
        byte[] decryptedBytes = cipher.doFinal(cipherText);
        return new String(decryptedBytes);
    }
    
    /**
     * 取得環境 key
     */
    private static SecretKey getSecretKeyFromEnv() {
    	String encodedKey = K;
    	SecretKey retrievedKey = new SecretKeySpec(Base64.getDecoder().decode(encodedKey), "AES");
    	return retrievedKey;
    }
    
    /**
     * 產生新的 key
     */
    public static SecretKey generateK() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(AES_KEY_SIZE, new SecureRandom());
        return keyGenerator.generateKey();
    }
    
    /**
     * 使用環境 key 加密 (return Base64)，Main執行 填入設定檔使用
     */
    public static String encrypt(String rawData) throws Exception {
    	byte[] b = encrypt(rawData, getSecretKeyFromEnv());
    	return Base64.getEncoder().encodeToString(b);
    }
    
    // ==============================================================================================================
    // ==============================================================================================================
    // ==============================================================================================================
    
    /** AES-GCM 加密 主體 */
    private static byte[] encrypt(String plainText, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

        // 產生隨機 IV
        byte[] iv = new byte[GCM_IV_LENGTH];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);

        // 設置 GCM 參數
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, gcmParameterSpec);

        // 附加驗證資料（可選）
        cipher.updateAAD("Chocolat".getBytes());

        // 執行加密
        byte[] cipherText = cipher.doFinal(plainText.getBytes());

        // 組合 IV + 密文
        ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + cipherText.length);
        byteBuffer.put(iv);
        byteBuffer.put(cipherText);
        return byteBuffer.array();
    }
    
    public static String getKeyBase64() {
    	return K;
    }
    
    // ==============================================================================================================
    
    // 屬性檔加解密範例(使用環境K)
    public static void main(String[] args) throws Exception {
    	 // 原始訊息
        String plainText = "雪雪雪雪";
        System.out.println("原始文字: " + plainText);
        // 加密
        String encryptedData = encrypt(plainText);
        System.out.println("加密後 (Base64): " + encryptedData);
        // 雪雪雪雪
    	String decryptedText = decrypt("si5s+0GFlBLj5jvC8RMMIYXAXRsXcbMc1LzlO3nHj2ZVT4IKSpA=");
        System.out.println("解密後: " + decryptedText);
	}

}
