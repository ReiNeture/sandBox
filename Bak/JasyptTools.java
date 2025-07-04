package com.vteam.webbank.util;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;

public class JasyptTools {

    public static void main(String[] args) {
        String secretKey = "xnTcAs1y9OttB9F";
        String plainText = "vteam";

        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();

        config.setPassword(secretKey);
        config.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setStringOutputType("base64");

        encryptor.setConfig(config);

        String encrypted = encryptor.encrypt(plainText);
        System.out.println("ENC(" + encrypted + ")");
    }
	
}
