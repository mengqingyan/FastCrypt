package com.kxd.fastcrypt;

/**
 * @author mengqingyan 2019/2/14
 */
public class DefaultCryptAlgorithm implements ICryptAlgorithm {
    public String encrypt(String arg) {
        return "encrypt: " + arg;
    }

    public String decrypt(String arg) {
        return "decrypt: " + arg;
    }
}
